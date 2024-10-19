package com.crs.expr2Jlogic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

/**
 * @author Crystas
 */

public class AntlrGenericExpressionUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode convertToJSONLogic(String expression) {
        ExpressionLexer lexer = new ExpressionLexer(CharStreams.fromString(expression));
        ExpressionParser parser = new ExpressionParser(new CommonTokenStream(lexer));
        ExpressionParser.ExprContext tree = parser.expr();
        return convertToJSONLogic(tree);

    }


    public static JsonNode convertToJSONLogic(ParseTree tree) {
        return visitForJSONLogic(tree);
    }

    private static JsonNode visitForJSONLogic(ParseTree node) {
        ObjectNode jsonNode = objectMapper.createObjectNode();
        ArrayNode arr = objectMapper.createArrayNode();


        if (node instanceof ExpressionParser.BooleanLiteralContext) {
            return Boolean.valueOf(node.getText()) ? BooleanNode.TRUE : BooleanNode.FALSE;
        }

        if (node instanceof ExpressionParser.NumberAtomContext) {
            String numberStr = node.getText();
            if (numberStr.contains(".")) {
                return new DoubleNode(Double.valueOf(numberStr));
            } else
                return new IntNode(Integer.valueOf(numberStr));
        }

        if (node instanceof TerminalNodeImpl) {
            return new TextNode(node.getText().replaceAll("\"$", "").replaceAll("^\"", ""));
        }

        if (node instanceof ExpressionParser.VariableContext) {
//            arr.add(node.getChild(0).getText());
            jsonNode.set("var", new TextNode( node.getChild(0).getText()));
            return jsonNode;
        }

        if ((node instanceof ExpressionParser.ExprContext)
                || node instanceof ExpressionParser.ToComparisonContext
                || node instanceof ExpressionParser.ToFunctionCallContext
                || node instanceof ExpressionParser.ToMembershipContext
                || node instanceof ExpressionParser.ToAtomContext
                || node instanceof ExpressionParser.BooleanContext
                || node instanceof ExpressionParser.StringAtomContext
                || (node instanceof ExpressionParser.ComparisonContext && node.getChildCount() == 1)
        ) {
            return visitForJSONLogic(node.getChild(0));
        }

        if (node instanceof ExpressionParser.ParensExprContext
                || node instanceof ExpressionParser.ListLiteralContext
        ) {
            return visitForJSONLogic(node.getChild(1));
        }

        if (node instanceof ExpressionParser.OrExpressionContext
                || node instanceof ExpressionParser.AndExpressionContext
                || node instanceof ExpressionParser.AddSubContext
                || node instanceof ExpressionParser.MulDivModContext
                || node instanceof ExpressionParser.InExpressionContext
                || (node instanceof ExpressionParser.ComparisonContext && node.getChildCount() == 3)
        ) {
            arr.add(visitForJSONLogic(node.getChild(0)));
            arr.add(visitForJSONLogic(node.getChild(2)));
            jsonNode.set(node.getChild(1).getText(), arr);
        }

        if (node instanceof ExpressionParser.CustomFunctionCallContext) {

            jsonNode.set(node.getChild(0).getText(), visitForJSONLogic(node.getChild(2)));
        }

        if (node instanceof ExpressionParser.ExprListContext) {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i).getText().equals(",")) continue;
                arr.add(visitForJSONLogic(node.getChild(i)));
            }
            return arr;
        }


        return jsonNode;
    }
}