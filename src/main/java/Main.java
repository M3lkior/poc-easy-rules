import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.mvel.MVELAction;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.JsonRuleDefinitionReader;

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
            "      \"FB_CALCULATED_SR = (TX_FB / 100) * PRX_ACHAT;\"\n" +
            "    ]\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"frais bancaire x2\",\n" +
            "    \"description\": \"\",\n" +
            "    \"priority\": 2,\n" +
            "    \"condition\": \"true\",\n" +
            "    \"actions\": [\n" +
            "      \"FB_CALCULATED_X2_SR = FB_CALCULATED_SR * 2\"\n" +
            "    ]\n" +
            "  }\n" +
            "]\n";

    public static void main(String[] args) throws Exception {
        // define facts
        Facts facts = new Facts();
        facts.put("TX_FB", "2");
        facts.put("PRX_ACHAT", "156");

        // define rules
        Rule fraisBancaire =new RuleBuilder().name("fraisBancaire")
                .when(Condition.TRUE)
                .then(new MVELAction("FB = 2 * 3")).build();
        Rule fraisBancaire2 =new RuleBuilder().name("fraisBancaire2")
                .when(Condition.TRUE)
                .then(new MVELAction("FB2 = FB * 2")).build();
        Rules rules = new Rules();
        rules.register(fraisBancaire);
        rules.register(fraisBancaire2);

        // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);

         System.out.println("DSL :");
         System.out.println("FB : " + facts.get("FB"));
         System.out.println("FB2 : " + facts.get("FB2"));

        MVELRuleFactory ruleFactory = new MVELRuleFactory(new JsonRuleDefinitionReader());
        Rules fbRules = ruleFactory.createRules(new FileReader("src/main/resources/fraisBancaire.json"));

        Rules stringRules = ruleFactory.createRules(new StringReader(STRING_RULES));

        //fbRules.execute(facts);
        rulesEngine.fire(fbRules, facts);
        System.out.println("JSON :");
        System.out.println(facts.get("FB_CALCULATED").toString());
        System.out.println(facts.get("FB_CALCULATED_X2").toString());
        //fbRules.execute(facts);

        rulesEngine.fire(stringRules, facts);
        System.out.println("JSON String:");
        System.out.println(facts.get("FB_CALCULATED").toString());

        System.out.println(facts.get("FB_CALCULATED_X2").toString());
        System.out.println(facts.get("FB_CALCULATED_3").toString());

        facts.put("TEST", "OK");
        facts.put("TEST2", "OK");
        rulesEngine.fire(fbRules, facts);

    }
}
