package eu.venthe.interview.nbp_web_proxy.api.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.venthe.interview.nbp_web_proxy.domain.CurrencyAccountId;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class CurrencyAccountIdConverter {
    public static final Module MODULE;

    static {
        var module = new SimpleModule();
        module.addSerializer(CurrencyAccountId.class, new CurrencyAccountIdConverter.Serializer());

        MODULE = module;
    }

    public static class Serializer extends JsonSerializer<CurrencyAccountId> {
        @Override
        public void serialize(CurrencyAccountId accountId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(accountId.value().toString());
        }
    }
}
