package be.ugent.mmlab.rml.condition.model.std;

import be.ugent.mmlab.rml.condition.model.BindingCondition;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.condition.model.BooleanCondition;
import java.util.Set;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * RML - Model
 *
 * @author andimou
 */
public class StdBooleanCondition extends StdCondition implements BooleanCondition {
    
    // Log
    private static final Logger log = 
            LogManager.getLogger(StdBooleanCondition.class);
    
    /**
     *
     * @param condition
     * @param value
     * @throws Exception
     */
    //TODO: Remove, deprecated
    public StdBooleanCondition(String condition, String value) throws Exception {
        setCondition(condition);
        setValue(value);
    }
    
    public StdBooleanCondition(String condition, 
            Set<BindingCondition> bindingConditions) throws Exception {
        setCondition(condition);
        setNestedBindingConditions(bindingConditions);
        //setNestedConditions(bindingConditions);
        //setValue(value);
    }
    
    /**
     *
     * @param condition
     * @param value
     * @param nestedConditions
     * @throws Exception
     */
    public StdBooleanCondition(String condition, String value, Set<Condition> nestedConditions) 
            throws Exception {
        setCondition(condition);
        setValue(value);
        setNestedConditions(nestedConditions);
    }
    
    private void setValue(String value) throws Exception {
        if (value == null) {
            throw new Exception("Exception: "
                    + "An equal condition must "
                    + "have a value.");
        }
        this.reference = value;
    }
    
    @Override
    public Set<BindingCondition> getBinding() {
        return this.bindingConditions;
    } 
    
    public void setNestedBindingConditions(Set<BindingCondition> bindingConditions) {
        this.bindingConditions = bindingConditions;
    }
    
}
