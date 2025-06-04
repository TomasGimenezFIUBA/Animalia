package com.tomasgimenez.citizen_common.kafka;

import java.io.IOException;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.stereotype.Component;

@Component
public class AvroDeserializer {
  public <T extends SpecificRecord> T deserialize(byte[] data, Class<T> clazz) {
    try {
      DatumReader<T> reader = new SpecificDatumReader<>(clazz);
      Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
      return reader.read(null, decoder);
    } catch (IOException e) {
      throw new RuntimeException("Error deserializing Avro", e);
    }
  }

}
