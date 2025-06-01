package com.tomasgimenez.citizen_command_service.kafka;

import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.DatumWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class AvroSerializer {

  public <T extends SpecificRecordBase> byte[] serialize(T record) {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      DatumWriter<T> writer = new SpecificDatumWriter<>(record.getSchema());
      BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
      writer.write(record, encoder);
      encoder.flush();
      return out.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to serialize Avro record", e);
    }
  }
}
