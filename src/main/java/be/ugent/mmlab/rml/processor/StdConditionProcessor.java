package be.ugent.mmlab.rml.processor;

import be.ugent.mmlab.rml.condition.model.BindingCondition;
import be.ugent.mmlab.rml.condition.model.Condition;
import be.ugent.mmlab.rml.logicalsourcehandler.termmap.TermMapProcessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RML Processor
 *
 * @author andimou
 */
public class StdConditionProcessor implements ConditionProcessor {
    
    // Log
    private static final Logger log = 
            LoggerFactory.getLogger(StdConditionProcessor.class);

    @Override
    public boolean processConditions(Object node, TermMapProcessor termMapProcessor, 
            Set<Condition> conditions) {
        Set<BindingCondition> bindings = new HashSet<BindingCondition>();
        Map<String, String> parameters;
        boolean result = false;

        iter: for (Condition condition : conditions) {
            if(condition.getClass().getSimpleName().equals("StdBindingCondition")){
                continue;
            }
            String expression = condition.getCondition();
            log.debug("expression " + expression);
            bindings = condition.getBindingConditions();
            
            for (BindingCondition binding : bindings) {
                parameters = processBindingConditions(node, termMapProcessor, bindings);

                String replacement = parameters.get(binding.getVariable());
                expression = expression.replaceAll(
                        "%%" + Pattern.quote(binding.getVariable()) + "%%",
                        replacement);

                //TODO: Properly handle the followings...
                if (expression.contains("!match")) {
                    result = processNotMatch(expression);
                    if (!result) {
                        break iter;
                    }
                } else if (expression.contains("match")) {
                    result = processMatch(expression);
                } else if (expression.contains("!contains")) {
                    result = processNotContains(expression);
                    if (!result) {
                        break iter;
                    }
                } else if (expression.contains("!length")) {
                    result = processNotLength(expression);
                    if (!result) {
                        break iter;
                    }
                }
            }
        }
        return result;
    }

    public Map<String, String> processBindingConditions(Object node, 
            TermMapProcessor termMapProcessor, Set<BindingCondition> bindingConditions) {
        Map<String, String> parameters = new HashMap<String, String>();
        ;
        for (BindingCondition bindingCondition : bindingConditions) {
            List<String> childValues = termMapProcessor.
                    extractValueFromNode(node, bindingCondition.getReference());

            for (String childValue : childValues) {
                parameters.put(
                        bindingCondition.getVariable(), childValue);
            }
        }

        return parameters;
    }
    
    //TODO: Move it separately
    public boolean processMatch(String expression){
        expression = expression.replace("match(", "").replace(")", "");
        String[] strings = expression.split(",");
        log.info("strings[0] " + strings[0]);
        log.info("strings[1] " + strings[1]);
        if (strings != null && strings.length > 1) {
            if (strings[0].equals(strings[1].replaceAll("\"", ""))) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public boolean processNotMatch(String expression) {
        expression = expression.replace("!match(", "").replace(")", "");
        log.debug("expression " + expression);
        String[] strings = expression.split(",");

        if (strings != null && strings.length > 1
                && strings[0].equals(strings[1].replaceAll("\"", ""))) {
            //log.debug("strings[0] " + strings[0]);
            //log.debug("strings[1] " + strings[1].replaceAll("\"", ""));
            return false;
        } else {
            return true;
        }
    }
    
    public boolean processNotContains(String expression){
        log.debug("Processing not contains condition...");
        expression = expression.replace("!contains(", "").replace(")", "");
        String[] strings = expression.split(",");
        
        if (strings != null && strings.length > 1) {
            if (strings[0].contains(strings[1].replaceAll("\"", ""))) {
                log.debug("strings[0] " + strings[0]);
                log.debug("strings[1] " + strings[1].replaceAll("\"", ""));
                return false;
            } else {
                return true;
            }
        }
        return true;
    }
    
    public boolean processNotLength(String expression){
        log.debug("Processing not length condition...");
        expression = expression.replace("!length(", "").replace(")", "");
        String[] strings = expression.split(",");
        
        if (strings != null && strings.length > 1) {
            if (strings[0].length() ==  
                    Integer.parseInt(strings[1].replaceAll("\"", ""))) {
                log.debug("strings[0] " + strings[0]);
                log.debug("strings[1] " + strings[1].replaceAll("\"", ""));
                return false;
            } else {
                return true;
            }
        }
        return true;
    }
}