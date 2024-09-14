package eu.venthe.interview.nbp_web_proxy.api.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.venthe.interview.nbp_web_proxy.shared_kernel.Money;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class MoneyConverter {
    public static final Module MODULE;

    static {
        var module = new SimpleModule();
        module.addSerializer(Money.class, new MoneyConverter.Serializer());

        MODULE = module;
    }

    public static class Serializer extends JsonSerializer<Money> {
        @Override
        public void serialize(Money money, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("amount", Money.FORMATTER.format(money.getAmount()));
            jsonGenerator.writeStringField("currency", money.getCurrency().toString());
            jsonGenerator.writeEndObject();
        }
    }
}
