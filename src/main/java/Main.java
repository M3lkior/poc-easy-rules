import org.jeasy.rules.api.*;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.JsonRuleDefinitionReader;

import java.io.FileReader;
import java.io.StringReader;

public class Main {

    private static String STRING_RULES = "[\n" +
            "  {\n" +
            "    \"name\": \"frais bancaire rule\",\n" +
            "    \"description\": \"\",\n" +
            "    \"priority\": 2,\n" +
            "    \"condition\": \"true\",\n" +
            "    \"actions\": [\n" +
            "      \"FB_CALCULATED = (TX_FB / 100) * PRX_ACHAT;\"\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"frais bancaire x2\",\n" +
            "    \"description\": \"\",\n" +
            "    \"priority\": 2,\n" +
            "    \"condition\": \"true\",\n" +
            "    \"actions\": [\n" +
            "      \"FB_CALCULATED_X2 = FB_CALCULATED * 2\"\n" +
            "    ]\n" +
            "  }\n" +
            "]\n";

    public static void main(String[] args) throws Exception {
        // define facts
        Facts facts = new Facts();
        facts.put("TX_FB", 2);
        facts.put("PRX_ACHAT", Double.valueOf("1.56"));

        // define rules
        Rule fraisBancaire =new RuleBuilder().name("fraisBancaire")
                .when(Condition.TRUE)
                .then(factsIn -> {
                Double txFraixBanque = Double.valueOf(factsIn.get("TX_FB").toString());
                Double prixAchat = Double.valueOf(factsIn.get("PRX_ACHAT").toString());

                Double result = (txFraixBanque / 100) * prixAchat;

                factsIn.put("result", result);

        }).build();
        Rules rules = new Rules();
        rules.register(fraisBancaire);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);


         System.out.println("DSL :");
         System.out.println(facts.get("result").toString());

        MVELRuleFactory ruleFactory = new MVELRuleFactory(new JsonRuleDefinitionReader());
        Rules fbRules = ruleFactory.createRules(new FileReader("src/main/resources/fraisBancaire.json"));

        Rules stringRules = ruleFactory.createRules(new StringReader(STRING_RULES));

        //fbRules.execute(facts);
        rulesEngine.fire(fbRules, facts);
        System.out.println("JSON :");
        System.out.println(facts.get("FB_CALCULATED").toString());
        //fbRules.execute(facts);
        rulesEngine.fire(stringRules, facts);
        System.out.println("JSON String:");
        System.out.println(facts.get("FB_CALCULATED").toString());

        System.out.println(facts.get("FB_CALCULATED_X2").toString());
    }
}
