package condition.util;

import condition.wrapper.Condition;
import condition.wrapper.Operator;
import condition.wrapper.RulePart;
import condition.wrapper.StringPiece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static condition.wrapper.Operator.*;

public class ConditionParser {

    public Condition parse(String rule) {
        List<String> nestedConditions = getMostNestedConditions(rule);
        List<RulePart> ruleParts = getNestedRuleParts(nestedConditions, rule);
        String result = replaceNestedParts(rule, ruleParts);
        return constructOneLevelCondition(result, ruleParts);
    }

    private List<String> getMostNestedConditions(String rule) {
        return Arrays.stream(rule.replaceAll("\\(", ",").replaceAll("\\)", ",").split(","))
                .filter(str -> !"".equals(str))
                .filter(str -> (!str.startsWith("AND")) && (!str.startsWith("OR")))
                .filter(str -> (!str.endsWith("AND")) && (!str.endsWith("OR")))
                .collect(Collectors.toList());
    }

    private List<RulePart> getNestedRuleParts(List<String> nestedConditions, String rule) {
        return nestedConditions.stream()
                .map(condition -> getNextLevel(rule, condition, constructCondition(condition)))
                .collect(Collectors.toList());
    }

    private Condition constructCondition(String condition) {
        Operator operator = findOperator(condition);
        List<String> conditions =
                Arrays.stream(condition.replace(operator.name(), "%").split("%")).collect(Collectors.toList());
        return new Condition(conditions.get(0), conditions.get(1), operator);
    }

    private Operator findOperator(String s) {
        if (s.contains("AND")) return AND;
        else if (s.contains("OR")) return OR;
        return SIMPLE;
    }

    private RulePart getNextLevel(String rule, String condition, Condition previous) {
        List<String> split = splitByCondition(rule, condition);
        if (split.size() > 2) {
            System.out.println("problem");
            throw new RuntimeException();
        }
        if (split.size() == 0) {
            return new RulePart("(" + condition + ")", previous);
        }

        List<StringPiece> nextParts = getNextPartOfCondition(split);
        if (isOneLevelLeft(nextParts)) {
            System.out.println("One level left");
            return new RulePart("(" + condition + ")", previous);
        }

        Condition conditionWithNewLevel = addNewLevel(previous, nextParts, condition);
        String newString = constructNewString(nextParts, condition);
        return getNextLevel(rule, newString, conditionWithNewLevel);
    }

    private Condition addNewLevel(Condition previous, List<StringPiece> nextParts, String condition) {
        if (nextParts.size() == 1) return constructNewComplex(nextParts.get(0), previous);
        String partRule = getPart(nextParts.get(0)) + "X0" + getPart(nextParts.get(1));
        return constructOneLevelCondition(
                partRule, List.of(new RulePart("(" + condition + ")", previous)));
    }

    private Condition constructNewComplex(StringPiece piece, Condition previous) {
        String condition = getPart(piece);
        Operator operator = findOperator(condition);
        List<String> list = Arrays.stream(condition.replace(operator.name(), "%").split("%")).collect(Collectors.toList());
        if (piece.getIndex() == 0) {
            return new Condition(list.get(0), previous, operator);
        }
        return new Condition(previous, list.get(1), operator);
    }


    private Condition constructOneLevelCondition(String rule, List<RulePart> complexes) {
        List<String> componentsSorted =
                Arrays.stream(rule.replace("OR", "$").replace("AND", "$").split("\\$")).collect(Collectors.toList());
        return constructComplexCondition(rule, complexes, componentsSorted);
    }

    private Condition constructComplexCondition(String rule, List<RulePart> complexes, List<String> components) {
        List<Operator> operators = getListOfOperators(rule, components);
        Condition condition = null;
        for (int j = 0; j < operators.size(); j++) {
            if (j == 0) {
                Object component1 =
                        isComplex(components.get(0))
                                ? getComplex(complexes, components.get(0))
                                : components.get(0);
                Object component2 =
                        isComplex(components.get(1))
                                ? getComplex(complexes, components.get(1))
                                : components.get(1);
                condition = new Condition(component1, component2, operators.get(j));
            } else {
                Condition previous = condition;
                Object component =
                        isComplex(components.get(j + 1))
                                ? getComplex(complexes, components.get(j + 1))
                                : components.get(j + 1);
                condition = new Condition(previous, component, operators.get(j));
            }
        }
        return condition;
    }

    private boolean isComplex(String condition) {
        return condition.startsWith("X");
    }

    private Condition getComplex(List<RulePart> parts, String complex) {
        int i = Integer.parseInt(complex.substring(1, 2));
        return parts.get(i).getCondition();
    }

    private List<Operator> getListOfOperators(String rule, List<String> components) {
        ArrayList<String> list = new ArrayList<>(components);
        list.remove(components.size() - 1);
        return list.stream()
                .map(
                        string -> {
                            List<String> strings = Arrays.stream(rule.replace(string, "%").split("%")).collect(Collectors.toList());
                            if (strings.get(1).startsWith("AND")) {
                                return AND;
                            } else if (strings.get(1).startsWith("OR")) {
                                return OR;
                            } else {
                                throw new RuntimeException();
                            }
                        })
                .collect(Collectors.toList());
    }

    private String getPart(StringPiece stringPiece) {
        String piece = stringPiece.getPiece();
        if (stringPiece.getIndex() == 0) {
            List<String> strings = Arrays.stream(piece.split("\\(")).collect(Collectors.toList());
            return strings.get(strings.size() - 1);
        }
        List<String> strings = Arrays.stream(piece.split("\\)")).collect(Collectors.toList());
        return strings.get(0);
    }

    private List<String> splitByCondition(String rule, String condition) {
        return Arrays.stream(rule.replace(condition, "%").split("%")).collect(Collectors.toList());
    }

    private List<StringPiece> getNextPartOfCondition(List<String> split) {
        return removeOnePairOfBreaks(split).stream()
                .filter(piece -> !piece.getPiece().endsWith("(") && !piece.getPiece().startsWith(")"))
                .collect(Collectors.toList());
    }

    private List<StringPiece> removeOnePairOfBreaks(List<String> split) {
        String first = split.get(0);
        StringPiece firstPiece = new StringPiece(first, 0);
        if (!(first.length() == 0)) {
            firstPiece = new StringPiece(first.substring(0, first.length() - 1), 0);
        }
        String second = split.get(1);
        StringPiece secondPiece = new StringPiece(second, 1);
        if (second.length() > 1) {
            secondPiece = new StringPiece(second.substring(1), 1);
        }
        return List.of(firstPiece, secondPiece);
    }

    private boolean isOneLevelLeft(List<StringPiece> result) {
        int a = getNumberOfChar(result.get(0).getPiece(), '(');
        int b = getNumberOfChar(result.get(0).getPiece(), ')');
        return a == b;
    }


    private String constructNewString(List<StringPiece> pieces, String str) {
        if (pieces.isEmpty()) throw new RuntimeException();
        if (pieces.size() > 1) {
            return getPart(pieces.get(0)) + "(" + str + ")" + getPart(pieces.get(1));
        }
        String piece = getPart(pieces.get(0));
        if (pieces.get(0).getIndex() == 0) {
            return piece + "(" + str + ")";
        }
        return "(" + str + ")" + piece;
    }

    private int getNumberOfChar(String piece, char c) {
        char[] chars = piece.toCharArray();
        int i = 0;
        for (char ch : chars) {
            if (ch == c) i++;
        }
        return i;
    }

    private String replaceNestedParts(String rule, List<RulePart> complexes) {
        String result = rule;
        for (int i = 0; i < complexes.size(); i++) {
            result = result.replace(complexes.get(i).getPart(), "X" + i);
        }
        return result;
    }
}
