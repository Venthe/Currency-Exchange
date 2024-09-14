package eu.venthe.interview.nbp_web_proxy.api.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.venthe.interview.nbp_web_proxy.api.PlainTextMoney;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class PlainTextMoneyConverter {
    public static final Module MODULE;

    static {
        var module = new SimpleModule();
        module.addSerializer(PlainTextMoney.class, new PlainTextMoneyConverter.Serializer());

        MODULE = module;
    }

    public static class Serializer extends JsonSerializer<PlainTextMoney> {
        @Override
        public void serialize(PlainTextMoney plainTextMoney, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // TODO: Add locale-aware formatter for money
            jsonGenerator.writeString(Money.FORMATTER.format(plainTextMoney.money().getAmount()) + " " + plainTextMoney.money().getCurrency().toString());
        }
    }
}
