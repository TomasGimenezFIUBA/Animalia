/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.tomasgimenez.animalia.avro;
@org.apache.avro.specific.AvroGenerated
public enum CitizenEventType implements org.apache.avro.generic.GenericEnumSymbol<CitizenEventType> {
  CREATED, UPDATED, DELETED  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"CitizenEventType\",\"namespace\":\"com.tomasgimenez.animalia.avro\",\"symbols\":[\"CREATED\",\"UPDATED\",\"DELETED\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
