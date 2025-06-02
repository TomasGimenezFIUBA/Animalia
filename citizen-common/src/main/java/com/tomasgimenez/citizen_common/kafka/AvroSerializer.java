package com.tomasgimenez.citizen_common.kafka;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;

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
