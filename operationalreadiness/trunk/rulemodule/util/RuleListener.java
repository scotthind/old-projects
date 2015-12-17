package rulemodule.util;

import java.util.List;

import util.DataType;

public interface RuleListener {
   void ruleDidFire(String ruleName, List<DataType> used, List<DataType> modified);
}
