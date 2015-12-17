package org.rowan.opawarenessvis.parser;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.rowan.opawarenessvis.data.AmountRule;
import org.rowan.opawarenessvis.data.AndRule;
import org.rowan.opawarenessvis.data.Condition;
import org.rowan.opawarenessvis.data.EntityOrAssetRule;
import org.rowan.opawarenessvis.data.NotRule;
import org.rowan.opawarenessvis.data.OrRule;
import org.rowan.opawarenessvis.data.Rule;
import org.rowan.opawarenessvis.data.SimpleRule;
import org.rowan.opawarenessvis.data.TempRule;

/**
 * Class RulesParser is responsible for parsing an XML file
 * with rules data into <code>Rules</code> objects.
 * 
 * @author Dan Urbano
 */
public class RulesParser implements XMLSubParser {
    /** An enumeration to define the modes that the parser can be in */
    private static enum Mode {NONE, OUTER, SYSTEM, MISSION, ENTITY, AND, OR,
                              NOT, AMOUNTOF, AMT_LESS, AMT_GREATER, AMT_EQUAL,
                              AMT_LESS_EQUAL, AMT_GREATER_EQUAL, AST_LESS,
                              AST_GREATER, AST_EQUAL, AST_LESS_EQUAL,
                              AST_GREATER_EQUAL, ASSET, CONDITION, SYSTEMRULE,
                              UNKNOWN}
    /** The name of the outer tag of the file */
    private static final String TAG_OUTER = "Rules";
    /** The name of the system tag */
    private static final String TAG_SYSTEM  = "System";
     /** The name of the mission tag */
    private static final String TAG_MISSION = "Mission";
    /** The name of the system rule tag */
    private static final String TAG_SYSTEM_RULE  = "SystemRule";
    /** The name of the entity tag */
    private static final String TAG_ENTITY  = "Entity";
    /** The name of the and tag*/
    private static final String TAG_AND  = "And";
    /** The name of the or tag*/
    private static final String TAG_OR  = "Or";
    /** The name of the not tag*/
    private static final String TAG_NOT  = "Not";
    /** The name of the amount of tag*/
    private static final String TAG_AMOUNTOF  = "AmountOf";
    /** The name of the condition tag*/
    private static final String TAG_CONDITION  = "Condition";
    /** The name of the asset tag*/
    private static final String TAG_ASSET  = "Asset";
    /** The name of the at least tag*/
    private static final String TAG_AT_LEAST  = "AtLeast";
    /** The name of the at most tag*/
    private static final String TAG_AT_MOST  = "AtMost";
    /** The name of the greater than tag*/
    private static final String TAG_GREATER_THAN  = "GreaterThan";
    /** The name of the less than tag*/
    private static final String TAG_LESS_THAN  = "LessThan";
    /** The name of the greater than or equal to tag*/
    private static final String TAG_GREATER_THAN_OR_EQUAL  = "GreaterThanOrEqualTo";
    /** The name of the less than or equal to tag*/
    private static final String TAG_LESS_THAN_OR_EQUAL  = "LessThanOrEqualTo";
    /** The name of the equal to tag*/
    private static final String TAG_EQUAL_TO  = "EqualTo";
    /** The name of the objective attribute */
    private static final String ATR_OBJECTIVE  = "objective";
    /** The name of the type attribute */
    private static final String ATR_TYPE  = "type";
    /** The name of the id attribute */
    private static final String ATR_ID  = "id";
    /** The stack containing the current nested tags */
    private Stack<String> stack = new Stack<String>();
    /** The map of system type to the map of mission object to a list of Rules that have been parsed */
    private Map<String,Map<String,List<EntityOrAssetRule>>> r_rules = new HashMap<String,Map<String,List<EntityOrAssetRule>>>();
    /** The mode of that parser is in */
    private Mode mode = Mode.NONE;
    /** The system id */
    private String r_sys_id = null;
    /** The mission objective */
    private String r_objective = null;
    /** The entity id */
    private String r_entity_id = null;
    /** The asset id */
    private String r_asset_id = null;
    /** The amount of type */
    private String r_amountOf_type = null;
    /** The stack of lists of rules */
    private Stack<List<Rule>> r_ruleStack = new Stack<List<Rule>>();
    /** The map of temp rules to the lists that need to be modified with the actual rules */
    private Map<TempRule, List<Rule>> r_tempRuleMap = new HashMap<TempRule, List<Rule>>();
    /** A list of all the parsed entity and asset rules */
    private List<EntityOrAssetRule> r_parsedRules = new ArrayList<EntityOrAssetRule>();
    /** A map of mission objectives to list of rules. */
    private Map<String, List<EntityOrAssetRule>> r_objectiveMap = new HashMap<String, List<EntityOrAssetRule>>();

    /**
     * Return all rules that were parsed using this parser. Be warned,
     * if this parser is reused, any newly parsed rules will be added
     * to this list unless clearRules() is called. The map returned maps a system
     * to it's mission maps. Each mission map maps a mission name to a list of
     * rules.
     * @return All parsed rules.
     */
    public Map<String, Map<String, List<EntityOrAssetRule>>> extractRules() {
        return r_rules;
    }
    
    /**
     * Clear all rules from this parser so it can be reused. Be warned,
     * you must call extractRules() before this method to retrieve a
     * list of any previously parsed rules.
     */
    public void clearRules() {
        r_rules = new HashMap<String,Map<String,List<EntityOrAssetRule>>>();
        r_ruleStack = new Stack<List<Rule>>();
        r_tempRuleMap = new HashMap<TempRule, List<Rule>>();
        r_parsedRules = new ArrayList<EntityOrAssetRule>();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String tag, Dictionary attributes, int line, int col) throws OpParseException {
        stack.push(tag);
        openTag(tag, attributes, line, col);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String tag, int line, int col) throws OpParseException {
        try {
            String s = stack.pop();
            if (s.equals(tag)) {
                closeTag(tag, line, col); //we closed the last opened tag, everything is OK
                return;
            }
            
            if (isOpen(tag)) {
                Errors.incorrectClose(tag, s, line, col); //we closed a tag, but not the last opened one
            } else {
                if (!isRecognizedTag(tag)) {
                    Warnings.unknownCloseTag(tag, line, col); //we closed a tag that doesn't exist
                    return;
                }
                Errors.invalidClose(tag, line, col); //we closed a tag that was never even opened
            }
            stack.push(s);
        } catch (EmptyStackException ese) {
            if (!isRecognizedTag(tag)) {
                Warnings.unknownCloseTag(tag, line, col); //we closed a tag that doesn't exist
                return;
            }
            Errors.invalidClose(tag, line, col); //we closed a tag but NO tags were opened
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void text(String text, int line, int col) {
        text = text.trim();
        if (text.isEmpty()) {
            return;
        }
        int intAmount;
        double doubleAmount;
        boolean intAmountError = false;
        boolean doubleAmountError = false;
        Condition cond = null;
        try {
            intAmount = Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            intAmount = -1;
            intAmountError = true;
        }
        try {
            doubleAmount = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            doubleAmount = -1;
            doubleAmountError = true;
        }
        boolean asset = (mode == Mode.AST_EQUAL || mode == Mode.AST_GREATER ||
                mode == Mode.AST_LESS || mode == Mode.AST_GREATER_EQUAL ||
                mode == Mode.AST_LESS_EQUAL);
        List<Rule> topList = null;
        if (!r_ruleStack.empty()) {
            topList = r_ruleStack.peek();
        }
        if (topList == null && !asset) {
            Warnings.general("An unknown error has occured. The text" + text +
                    " does not seem to be inside a tag which is inside a " +
                    "<And>, <Or>, or <Not> tag. Skipping.", line, col);
            return;
        }
        
        switch (mode) {
            case CONDITION:
                for (EntityOrAssetRule parsedRule : r_parsedRules) {
                    if (parsedRule.getComponentId().equals(text)) {
                        topList.add(parsedRule);
                        return;
                    }
                }
                // the rule inside the condition does not exist yet
                r_tempRuleMap.put(new TempRule(text), topList);
                break;
            case AMT_LESS:
                if (intAmount >= 1) {
                    cond = Condition.LessThan;
                }
                break;
            case AMT_GREATER:
                if (intAmount >= 0) {
                    cond = Condition.GreaterThan;
                }
                break;
            case AMT_LESS_EQUAL:
                if (intAmount >= 0) {
                    cond = Condition.LessThanOrEqualTo;
                }
                break;
            case AMT_GREATER_EQUAL:
                if (intAmount >= 0) {
                    cond = Condition.GreaterThanOrEqualTo;
                }
                break;
            case AMT_EQUAL:
                if (intAmount >= 0) {
                    cond = Condition.EqualTo;
                }
                break;
            case AST_LESS:
                if (doubleAmount > 0) {
                    cond = Condition.LessThan;
                }
                break;
            case AST_GREATER:
                if (doubleAmount >= 0) {
                    cond = Condition.GreaterThan;
                }
                break;
            case AST_LESS_EQUAL:
                if (doubleAmount >= 0) {
                    cond = Condition.LessThanOrEqualTo;
                }
                break;
            case AST_GREATER_EQUAL:
                if (doubleAmount >= 0) {
                    cond = Condition.GreaterThanOrEqualTo;
                }
                break;
            case AST_EQUAL:
                if (doubleAmount >= 0) {
                    cond = Condition.EqualTo;
                }
                break;
        }

        if (!asset && !intAmountError && cond != null) {
            AmountRule rule = new AmountRule(r_entity_id, r_amountOf_type, cond, intAmount);
            topList.add(rule);
        } else if (asset && !doubleAmountError && cond != null) {
            SimpleRule rule = new SimpleRule(r_asset_id, cond, doubleAmount);
            r_parsedRules.add(rule);
        } else if (mode != Mode.CONDITION) {
            Warnings.general("The amount \"" + text + "\" was not a valid number. Skipping.", line, col);
        }
    }

    /**
     * Handle an opening tag.
     * @param tag The tag that was opened.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void openTag(String tag, Dictionary attributes, int line, int col) throws OpParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case NONE:
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_SYSTEM)) {
            switch (mode) {
                case OUTER:
                    r_objectiveMap = new HashMap<String, List<EntityOrAssetRule>>();
                    r_sys_id = (String)attributes.get(ATR_ID);
                    r_objective = null;
                    r_entity_id = null;
                    r_asset_id = null;
                    r_amountOf_type = null;
                    r_ruleStack = new Stack<List<Rule>>();
                    mode = Mode.SYSTEM;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_MISSION)) {
            switch (mode) {
                case SYSTEM:
                    r_objective = (String)attributes.get(ATR_OBJECTIVE);
                    r_entity_id = null;
                    r_asset_id = null;
                    r_amountOf_type = null;
                    r_ruleStack = new Stack<List<Rule>>();
                    r_parsedRules = new ArrayList<EntityOrAssetRule>();
                    mode = Mode.MISSION;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_SYSTEM_RULE)) {
            switch (mode) {
                case MISSION:
                    r_entity_id = r_sys_id;
                    r_ruleStack = new Stack<List<Rule>>();
                    r_amountOf_type = null;
                    mode = Mode.SYSTEMRULE;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_ENTITY)) {
            switch (mode) {
                case MISSION:
                    r_entity_id = (String)attributes.get(ATR_ID);
                    r_ruleStack = new Stack<List<Rule>>();
                    r_amountOf_type = null;
                    mode = Mode.ENTITY;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_ASSET)) {
            switch (mode) {
                case MISSION:
                    r_asset_id = (String)attributes.get(ATR_ID);
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_AND)) {
            switch (mode) {
                case SYSTEMRULE:
                case ENTITY:
                case AND:
                case OR:
                case NOT:
                    r_ruleStack.push(new ArrayList<Rule>());
                    mode = Mode.AND;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_OR)) {
            switch (mode) {
                case SYSTEMRULE:
                case ENTITY:
                case AND:
                case OR:
                case NOT:
                    r_ruleStack.push(new ArrayList<Rule>());
                    mode = Mode.OR;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_NOT)) {
            switch (mode) {
                case AND:
                case OR:
                case NOT:
                    r_ruleStack.push(new ArrayList<Rule>());
                    mode = Mode.NOT;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_CONDITION)) {
            switch (mode) {
                case AND:
                case OR:
                case NOT:
                    mode = Mode.CONDITION;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_AMOUNTOF)) {
            switch (mode) {
                case AND:
                case OR:
                case NOT:
                    r_amountOf_type = (String)attributes.get(ATR_TYPE);
                    mode = Mode.AMOUNTOF;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_ASSET)) {
            switch (mode) {
                case MISSION:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_AT_LEAST) || isEqual(tag, TAG_GREATER_THAN_OR_EQUAL)) {
            switch (mode) {
                case AMOUNTOF:
                    mode = Mode.AMT_GREATER_EQUAL;
                    break;
                case ASSET:
                    mode = Mode.AST_GREATER_EQUAL;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_AT_MOST) || isEqual(tag, TAG_LESS_THAN_OR_EQUAL)) {
            switch (mode) {
                case AMOUNTOF:
                    mode = Mode.AMT_LESS_EQUAL;
                    break;
                case ASSET:
                    mode = Mode.AST_LESS_EQUAL;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_GREATER_THAN)) {
            switch (mode) {
                case AMOUNTOF:
                    mode = Mode.AMT_GREATER;
                    break;
                case ASSET:
                    mode = Mode.AST_GREATER;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_LESS_THAN)) {
            switch (mode) {
                case AMOUNTOF:
                    mode = Mode.AMT_LESS;
                    break;
                case ASSET:
                    mode = Mode.AST_LESS;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else if (isEqual(tag, TAG_EQUAL_TO)) {
            switch (mode) {
                case AMOUNTOF:
                    mode = Mode.AMT_EQUAL;
                    break;
                case ASSET:
                    mode = Mode.AST_EQUAL;
                    break;
                default:
                    Errors.unexpectedOpen(tag, line, col);
                    stack.pop();
            }
        } else {
            Warnings.unknownOpenTag(tag, line, col);
        }
    }
    
    /**
     * Handle a closing tag.
     * @param tag The tag that was closed.
     * @param line The line number that the tag is on.
     * @param col The column number that the tag is on.
     */
    private void closeTag(String tag, int line, int col) throws OpParseException {
        if (isEqual(tag, TAG_OUTER)) {
            switch (mode) {
                case OUTER:
                    if (stack.isEmpty()) {
                        mode = Mode.NONE;
                    } else {
                        Errors.expectedToHaveClosedAll(tag, line, col);
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_SYSTEM)) {
            switch (mode) {
                case SYSTEM:
                    r_rules.put(r_sys_id, r_objectiveMap);
                    mode = Mode.OUTER;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
                    return;
            }
        } else if (isEqual(tag, TAG_MISSION)) {
            switch (mode) {
                case MISSION:
                    boolean doneReplacingTempRules = false;
                    while (!doneReplacingTempRules) {
                        List<TempRule> deleteFromTempRule = new ArrayList<TempRule>();
                        for (TempRule tempRule : r_tempRuleMap.keySet()) {
                            for (EntityOrAssetRule rule : r_parsedRules) {
                                if (tempRule.getComponentID().equals(rule.getComponentId())) {
                                    r_tempRuleMap.get(tempRule).remove(tempRule);
                                    r_tempRuleMap.get(tempRule).add(rule);
                                    deleteFromTempRule.add(tempRule);
                                    break;
                                }
                            }
                        }
                        if (deleteFromTempRule.isEmpty() && !r_tempRuleMap.keySet().isEmpty()) {
                            //TODO ERROR OUT THAT TEMP RULES COULDN'T BE REPLACED
                            System.err.println("Couldn't replace all temp rules...exiting...");
                            System.exit(-1);
                        }
                        for (TempRule tempRule : deleteFromTempRule) {
                            r_tempRuleMap.remove(tempRule);
                        }
                        if (r_tempRuleMap.keySet().isEmpty()) {
                            doneReplacingTempRules = true;
                        }
                    }
                    r_objectiveMap.put(r_objective, r_parsedRules);
                    mode = Mode.SYSTEM;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_SYSTEM_RULE)) {
            switch (mode) {
                case SYSTEMRULE:
                    mode = Mode.MISSION;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_ENTITY)) {
            switch (mode) {
                case ENTITY:
                    mode = Mode.MISSION;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_ASSET)) {
            switch (mode) {
                case ASSET:
                    mode = Mode.MISSION;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_AND)) {
            switch (mode) {
                case AND:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_AND) ||
                            isEqual(prevTag, TAG_OR) ||
                            isEqual(prevTag, TAG_NOT)) {
                        AndRule rule = new AndRule(r_ruleStack.pop(), null);
                        r_ruleStack.peek().add(rule);
                        if (isEqual(prevTag, TAG_AND)) {
                            mode = Mode.AND;
                        } else if (isEqual(prevTag, TAG_OR)) {
                            mode = Mode.OR;
                        } else if (isEqual(prevTag, TAG_NOT)) {
                            mode = Mode.NOT;
                        }
                    } else if (isEqual(prevTag, TAG_ENTITY) ||
                            isEqual(prevTag, TAG_SYSTEM_RULE)) {
                        AndRule rule = new AndRule(r_ruleStack.pop(), r_entity_id);
                        r_parsedRules.add(rule);
                        if (isEqual(prevTag, TAG_ENTITY)) {
                            mode = Mode.ENTITY;
                        } else if (isEqual(prevTag, TAG_SYSTEM_RULE)) {
                            mode = Mode.SYSTEMRULE;
                        }
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_OR)) {
            switch (mode) {
                case OR:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_AND) ||
                            isEqual(prevTag, TAG_OR) ||
                            isEqual(prevTag, TAG_NOT)) {
                        OrRule rule = new OrRule(r_ruleStack.pop(), null);
                        r_ruleStack.peek().add(rule);
                        if (isEqual(prevTag, TAG_AND)) {
                            mode = Mode.AND;
                        } else if (isEqual(prevTag, TAG_OR)) {
                            mode = Mode.OR;
                        } else if (isEqual(prevTag, TAG_NOT)) {
                            mode = Mode.NOT;
                        }
                    } else if (isEqual(prevTag, TAG_ENTITY) ||
                            isEqual(prevTag, TAG_SYSTEM_RULE)) {
                        OrRule rule = new OrRule(r_ruleStack.pop(), r_entity_id);
                        r_parsedRules.add(rule);
                        if (isEqual(prevTag, TAG_ENTITY)) {
                            mode = Mode.ENTITY;
                        } else if (isEqual(prevTag, TAG_SYSTEM_RULE)) {
                            mode = Mode.SYSTEMRULE;
                        }
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_NOT)) {
            switch (mode) {
                case NOT:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_AND) ||
                            isEqual(prevTag, TAG_OR) ||
                            isEqual(prevTag, TAG_NOT)) {
                        NotRule rule = new NotRule(r_ruleStack.pop());
                        r_ruleStack.peek().add(rule);
                        if (isEqual(prevTag, TAG_AND)) {
                            mode = Mode.AND;
                        } else if (isEqual(prevTag, TAG_OR)) {
                            mode = Mode.OR;
                        } else if (isEqual(prevTag, TAG_NOT)) {
                            mode = Mode.NOT;
                        }
                    }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_CONDITION)) {
            switch (mode) {
                case CONDITION:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_AND)) {
                            mode = Mode.AND;
                        } else if (isEqual(prevTag, TAG_OR)) {
                            mode = Mode.OR;
                        } else if (isEqual(prevTag, TAG_NOT)) {
                            mode = Mode.NOT;
                        }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_AMOUNTOF)) {
            switch (mode) {
                case AMOUNTOF:
                    String prevTag = stack.peek();
                    if (isEqual(prevTag, TAG_AND)) {
                            mode = Mode.AND;
                        } else if (isEqual(prevTag, TAG_OR)) {
                            mode = Mode.OR;
                        } else if (isEqual(prevTag, TAG_NOT)) {
                            mode = Mode.NOT;
                        }
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_EQUAL_TO)) {
            switch (mode) {
                case AMT_EQUAL:
                    mode = Mode.AMOUNTOF;
                    break;
                case AST_EQUAL:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_LESS_THAN)) {
            switch (mode) {
                case AMT_LESS:
                    mode = Mode.AMOUNTOF;
                    break;
                case AST_LESS:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_GREATER_THAN)) {
            switch (mode) {
                case AMT_GREATER:
                    mode = Mode.AMOUNTOF;
                    break;
                case AST_GREATER:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_GREATER_THAN_OR_EQUAL) || isEqual(tag, TAG_AT_LEAST)) {
            switch (mode) {
                case AMT_GREATER_EQUAL:
                    mode = Mode.AMOUNTOF;
                    break;
                case AST_GREATER_EQUAL:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else if (isEqual(tag, TAG_LESS_THAN_OR_EQUAL) || isEqual(tag, TAG_AT_MOST)) {
            switch (mode) {
                case AMT_LESS_EQUAL:
                    mode = Mode.AMOUNTOF;
                    break;
                case AST_LESS_EQUAL:
                    mode = Mode.ASSET;
                    break;
                default:
                    Errors.unknownOnClose(tag, line, col);
            }
        } else {
            Warnings.unknownCloseTag(tag, line, col);
        }
    }
    
    /**
     * Test to see if two strings are equal (ignoring external spaces and
     * using a case insensitive approach).
     * @param a The first string to test.
     * @param b The second string to test.
     * @return true if a and b are equal according to the definition in the
     *         method description.
     */
    private boolean isEqual(String a, String b) {
        return (a.trim().toLowerCase().equals(b.trim().toLowerCase()));
    }
    
    /**
     * Determine if a given tag was opened, but not yet closed.
     * @param tag The tag to search for.
     * @return true if tag was located in the stack of opened tags.
     */
    private boolean isOpen(String tag) {
        // try to find elem by emptying stack. if found, s will equal tag
        String s = stack.peek();
        Stack<String> temp = new Stack<String>();
        while ((!s.equals(tag)) && (!stack.isEmpty())) {
            s = temp.push(stack.pop());
        }
        
        // put all emptied items back into the stack
        while (!temp.isEmpty()) {
            stack.push(temp.pop());
        }
        
        // if tag was found, it will be equal to s
        return (s.equals(tag));
    }
    
    /**
     * Test if a given tag name is recognized.
     * @param tag The tag name to test.
     * @return true if it is one of the recognized tag names.
     */
    private boolean isRecognizedTag(String tag) {
        return (
            tag.equals(TAG_OUTER) ||
            tag.equals(TAG_SYSTEM) ||
            tag.equals(TAG_MISSION) ||
            tag.equals(TAG_SYSTEM_RULE) || 
            tag.equals(TAG_AND) || 
            tag.equals(TAG_OR) || 
            tag.equals(TAG_NOT) || 
            tag.equals(TAG_CONDITION) || 
            tag.equals(TAG_ENTITY) || 
            tag.equals(TAG_ASSET) || 
            tag.equals(TAG_AMOUNTOF) || 
            tag.equals(TAG_LESS_THAN) || 
            tag.equals(TAG_LESS_THAN_OR_EQUAL) || 
            tag.equals(TAG_GREATER_THAN) || 
            tag.equals(TAG_GREATER_THAN_OR_EQUAL) || 
            tag.equals(TAG_AT_LEAST) || 
            tag.equals(TAG_AT_MOST) || 
            tag.equals(TAG_EQUAL_TO)
        );
    }
}
