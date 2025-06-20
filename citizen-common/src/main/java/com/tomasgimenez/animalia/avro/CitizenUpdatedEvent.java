/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.tomasgimenez.animalia.avro;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class CitizenUpdatedEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 1555516005741269361L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"CitizenUpdatedEvent\",\"namespace\":\"com.tomasgimenez.animalia.avro\",\"fields\":[{\"name\":\"eventId\",\"type\":\"string\"},{\"name\":\"timestamp\",\"type\":\"string\"},{\"name\":\"source\",\"type\":\"string\"},{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"name\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"hasHumanPet\",\"type\":[\"null\",\"boolean\"],\"default\":null},{\"name\":\"species\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Species\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"weight\",\"type\":\"double\"},{\"name\":\"height\",\"type\":\"double\"}]}],\"default\":null},{\"name\":\"roleNames\",\"type\":[\"null\",{\"type\":\"array\",\"items\":\"string\"}],\"default\":null}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<CitizenUpdatedEvent> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<CitizenUpdatedEvent> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<CitizenUpdatedEvent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<CitizenUpdatedEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<CitizenUpdatedEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this CitizenUpdatedEvent to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a CitizenUpdatedEvent from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a CitizenUpdatedEvent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static CitizenUpdatedEvent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence eventId;
  private java.lang.CharSequence timestamp;
  private java.lang.CharSequence source;
  private java.lang.CharSequence id;
  private java.lang.CharSequence name;
  private java.lang.Boolean hasHumanPet;
  private com.tomasgimenez.animalia.avro.Species species;
  private java.util.List<java.lang.CharSequence> roleNames;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public CitizenUpdatedEvent() {}

  /**
   * All-args constructor.
   * @param eventId The new value for eventId
   * @param timestamp The new value for timestamp
   * @param source The new value for source
   * @param id The new value for id
   * @param name The new value for name
   * @param hasHumanPet The new value for hasHumanPet
   * @param species The new value for species
   * @param roleNames The new value for roleNames
   */
  public CitizenUpdatedEvent(java.lang.CharSequence eventId, java.lang.CharSequence timestamp, java.lang.CharSequence source, java.lang.CharSequence id, java.lang.CharSequence name, java.lang.Boolean hasHumanPet, com.tomasgimenez.animalia.avro.Species species, java.util.List<java.lang.CharSequence> roleNames) {
    this.eventId = eventId;
    this.timestamp = timestamp;
    this.source = source;
    this.id = id;
    this.name = name;
    this.hasHumanPet = hasHumanPet;
    this.species = species;
    this.roleNames = roleNames;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return eventId;
    case 1: return timestamp;
    case 2: return source;
    case 3: return id;
    case 4: return name;
    case 5: return hasHumanPet;
    case 6: return species;
    case 7: return roleNames;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: eventId = (java.lang.CharSequence)value$; break;
    case 1: timestamp = (java.lang.CharSequence)value$; break;
    case 2: source = (java.lang.CharSequence)value$; break;
    case 3: id = (java.lang.CharSequence)value$; break;
    case 4: name = (java.lang.CharSequence)value$; break;
    case 5: hasHumanPet = (java.lang.Boolean)value$; break;
    case 6: species = (com.tomasgimenez.animalia.avro.Species)value$; break;
    case 7: roleNames = (java.util.List<java.lang.CharSequence>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'eventId' field.
   * @return The value of the 'eventId' field.
   */
  public java.lang.CharSequence getEventId() {
    return eventId;
  }


  /**
   * Sets the value of the 'eventId' field.
   * @param value the value to set.
   */
  public void setEventId(java.lang.CharSequence value) {
    this.eventId = value;
  }

  /**
   * Gets the value of the 'timestamp' field.
   * @return The value of the 'timestamp' field.
   */
  public java.lang.CharSequence getTimestamp() {
    return timestamp;
  }


  /**
   * Sets the value of the 'timestamp' field.
   * @param value the value to set.
   */
  public void setTimestamp(java.lang.CharSequence value) {
    this.timestamp = value;
  }

  /**
   * Gets the value of the 'source' field.
   * @return The value of the 'source' field.
   */
  public java.lang.CharSequence getSource() {
    return source;
  }


  /**
   * Sets the value of the 'source' field.
   * @param value the value to set.
   */
  public void setSource(java.lang.CharSequence value) {
    this.source = value;
  }

  /**
   * Gets the value of the 'id' field.
   * @return The value of the 'id' field.
   */
  public java.lang.CharSequence getId() {
    return id;
  }


  /**
   * Sets the value of the 'id' field.
   * @param value the value to set.
   */
  public void setId(java.lang.CharSequence value) {
    this.id = value;
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'hasHumanPet' field.
   * @return The value of the 'hasHumanPet' field.
   */
  public java.lang.Boolean getHasHumanPet() {
    return hasHumanPet;
  }


  /**
   * Sets the value of the 'hasHumanPet' field.
   * @param value the value to set.
   */
  public void setHasHumanPet(java.lang.Boolean value) {
    this.hasHumanPet = value;
  }

  /**
   * Gets the value of the 'species' field.
   * @return The value of the 'species' field.
   */
  public com.tomasgimenez.animalia.avro.Species getSpecies() {
    return species;
  }


  /**
   * Sets the value of the 'species' field.
   * @param value the value to set.
   */
  public void setSpecies(com.tomasgimenez.animalia.avro.Species value) {
    this.species = value;
  }

  /**
   * Gets the value of the 'roleNames' field.
   * @return The value of the 'roleNames' field.
   */
  public java.util.List<java.lang.CharSequence> getRoleNames() {
    return roleNames;
  }


  /**
   * Sets the value of the 'roleNames' field.
   * @param value the value to set.
   */
  public void setRoleNames(java.util.List<java.lang.CharSequence> value) {
    this.roleNames = value;
  }

  /**
   * Creates a new CitizenUpdatedEvent RecordBuilder.
   * @return A new CitizenUpdatedEvent RecordBuilder
   */
  public static com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder newBuilder() {
    return new com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder();
  }

  /**
   * Creates a new CitizenUpdatedEvent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new CitizenUpdatedEvent RecordBuilder
   */
  public static com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder newBuilder(com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder other) {
    if (other == null) {
      return new com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder();
    } else {
      return new com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder(other);
    }
  }

  /**
   * Creates a new CitizenUpdatedEvent RecordBuilder by copying an existing CitizenUpdatedEvent instance.
   * @param other The existing instance to copy.
   * @return A new CitizenUpdatedEvent RecordBuilder
   */
  public static com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder newBuilder(com.tomasgimenez.animalia.avro.CitizenUpdatedEvent other) {
    if (other == null) {
      return new com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder();
    } else {
      return new com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder(other);
    }
  }

  /**
   * RecordBuilder for CitizenUpdatedEvent instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CitizenUpdatedEvent>
    implements org.apache.avro.data.RecordBuilder<CitizenUpdatedEvent> {

    private java.lang.CharSequence eventId;
    private java.lang.CharSequence timestamp;
    private java.lang.CharSequence source;
    private java.lang.CharSequence id;
    private java.lang.CharSequence name;
    private java.lang.Boolean hasHumanPet;
    private com.tomasgimenez.animalia.avro.Species species;
    private com.tomasgimenez.animalia.avro.Species.Builder speciesBuilder;
    private java.util.List<java.lang.CharSequence> roleNames;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.eventId)) {
        this.eventId = data().deepCopy(fields()[0].schema(), other.eventId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.source)) {
        this.source = data().deepCopy(fields()[2].schema(), other.source);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.id)) {
        this.id = data().deepCopy(fields()[3].schema(), other.id);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.name)) {
        this.name = data().deepCopy(fields()[4].schema(), other.name);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.hasHumanPet)) {
        this.hasHumanPet = data().deepCopy(fields()[5].schema(), other.hasHumanPet);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.species)) {
        this.species = data().deepCopy(fields()[6].schema(), other.species);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (other.hasSpeciesBuilder()) {
        this.speciesBuilder = com.tomasgimenez.animalia.avro.Species.newBuilder(other.getSpeciesBuilder());
      }
      if (isValidValue(fields()[7], other.roleNames)) {
        this.roleNames = data().deepCopy(fields()[7].schema(), other.roleNames);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
    }

    /**
     * Creates a Builder by copying an existing CitizenUpdatedEvent instance
     * @param other The existing instance to copy.
     */
    private Builder(com.tomasgimenez.animalia.avro.CitizenUpdatedEvent other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.eventId)) {
        this.eventId = data().deepCopy(fields()[0].schema(), other.eventId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[1].schema(), other.timestamp);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.source)) {
        this.source = data().deepCopy(fields()[2].schema(), other.source);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.id)) {
        this.id = data().deepCopy(fields()[3].schema(), other.id);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.name)) {
        this.name = data().deepCopy(fields()[4].schema(), other.name);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.hasHumanPet)) {
        this.hasHumanPet = data().deepCopy(fields()[5].schema(), other.hasHumanPet);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.species)) {
        this.species = data().deepCopy(fields()[6].schema(), other.species);
        fieldSetFlags()[6] = true;
      }
      this.speciesBuilder = null;
      if (isValidValue(fields()[7], other.roleNames)) {
        this.roleNames = data().deepCopy(fields()[7].schema(), other.roleNames);
        fieldSetFlags()[7] = true;
      }
    }

    /**
      * Gets the value of the 'eventId' field.
      * @return The value.
      */
    public java.lang.CharSequence getEventId() {
      return eventId;
    }


    /**
      * Sets the value of the 'eventId' field.
      * @param value The value of 'eventId'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setEventId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.eventId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'eventId' field has been set.
      * @return True if the 'eventId' field has been set, false otherwise.
      */
    public boolean hasEventId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'eventId' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearEventId() {
      eventId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'timestamp' field.
      * @return The value.
      */
    public java.lang.CharSequence getTimestamp() {
      return timestamp;
    }


    /**
      * Sets the value of the 'timestamp' field.
      * @param value The value of 'timestamp'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setTimestamp(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.timestamp = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'timestamp' field has been set.
      * @return True if the 'timestamp' field has been set, false otherwise.
      */
    public boolean hasTimestamp() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'timestamp' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearTimestamp() {
      timestamp = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'source' field.
      * @return The value.
      */
    public java.lang.CharSequence getSource() {
      return source;
    }


    /**
      * Sets the value of the 'source' field.
      * @param value The value of 'source'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setSource(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.source = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'source' field has been set.
      * @return True if the 'source' field has been set, false otherwise.
      */
    public boolean hasSource() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'source' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearSource() {
      source = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'id' field.
      * @return The value.
      */
    public java.lang.CharSequence getId() {
      return id;
    }


    /**
      * Sets the value of the 'id' field.
      * @param value The value of 'id'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setId(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.id = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'id' field has been set.
      * @return True if the 'id' field has been set, false otherwise.
      */
    public boolean hasId() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'id' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearId() {
      id = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setName(java.lang.CharSequence value) {
      validate(fields()[4], value);
      this.name = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearName() {
      name = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'hasHumanPet' field.
      * @return The value.
      */
    public java.lang.Boolean getHasHumanPet() {
      return hasHumanPet;
    }


    /**
      * Sets the value of the 'hasHumanPet' field.
      * @param value The value of 'hasHumanPet'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setHasHumanPet(java.lang.Boolean value) {
      validate(fields()[5], value);
      this.hasHumanPet = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'hasHumanPet' field has been set.
      * @return True if the 'hasHumanPet' field has been set, false otherwise.
      */
    public boolean hasHasHumanPet() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'hasHumanPet' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearHasHumanPet() {
      hasHumanPet = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'species' field.
      * @return The value.
      */
    public com.tomasgimenez.animalia.avro.Species getSpecies() {
      return species;
    }


    /**
      * Sets the value of the 'species' field.
      * @param value The value of 'species'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setSpecies(com.tomasgimenez.animalia.avro.Species value) {
      validate(fields()[6], value);
      this.speciesBuilder = null;
      this.species = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'species' field has been set.
      * @return True if the 'species' field has been set, false otherwise.
      */
    public boolean hasSpecies() {
      return fieldSetFlags()[6];
    }

    /**
     * Gets the Builder instance for the 'species' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public com.tomasgimenez.animalia.avro.Species.Builder getSpeciesBuilder() {
      if (speciesBuilder == null) {
        if (hasSpecies()) {
          setSpeciesBuilder(com.tomasgimenez.animalia.avro.Species.newBuilder(species));
        } else {
          setSpeciesBuilder(com.tomasgimenez.animalia.avro.Species.newBuilder());
        }
      }
      return speciesBuilder;
    }

    /**
     * Sets the Builder instance for the 'species' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setSpeciesBuilder(com.tomasgimenez.animalia.avro.Species.Builder value) {
      clearSpecies();
      speciesBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'species' field has an active Builder instance
     * @return True if the 'species' field has an active Builder instance
     */
    public boolean hasSpeciesBuilder() {
      return speciesBuilder != null;
    }

    /**
      * Clears the value of the 'species' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearSpecies() {
      species = null;
      speciesBuilder = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'roleNames' field.
      * @return The value.
      */
    public java.util.List<java.lang.CharSequence> getRoleNames() {
      return roleNames;
    }


    /**
      * Sets the value of the 'roleNames' field.
      * @param value The value of 'roleNames'.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder setRoleNames(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[7], value);
      this.roleNames = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'roleNames' field has been set.
      * @return True if the 'roleNames' field has been set, false otherwise.
      */
    public boolean hasRoleNames() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'roleNames' field.
      * @return This builder.
      */
    public com.tomasgimenez.animalia.avro.CitizenUpdatedEvent.Builder clearRoleNames() {
      roleNames = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CitizenUpdatedEvent build() {
      try {
        CitizenUpdatedEvent record = new CitizenUpdatedEvent();
        record.eventId = fieldSetFlags()[0] ? this.eventId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.timestamp = fieldSetFlags()[1] ? this.timestamp : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.source = fieldSetFlags()[2] ? this.source : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.id = fieldSetFlags()[3] ? this.id : (java.lang.CharSequence) defaultValue(fields()[3]);
        record.name = fieldSetFlags()[4] ? this.name : (java.lang.CharSequence) defaultValue(fields()[4]);
        record.hasHumanPet = fieldSetFlags()[5] ? this.hasHumanPet : (java.lang.Boolean) defaultValue(fields()[5]);
        if (speciesBuilder != null) {
          try {
            record.species = this.speciesBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("species"));
            throw e;
          }
        } else {
          record.species = fieldSetFlags()[6] ? this.species : (com.tomasgimenez.animalia.avro.Species) defaultValue(fields()[6]);
        }
        record.roleNames = fieldSetFlags()[7] ? this.roleNames : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[7]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<CitizenUpdatedEvent>
    WRITER$ = (org.apache.avro.io.DatumWriter<CitizenUpdatedEvent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<CitizenUpdatedEvent>
    READER$ = (org.apache.avro.io.DatumReader<CitizenUpdatedEvent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.eventId);

    out.writeString(this.timestamp);

    out.writeString(this.source);

    out.writeString(this.id);

    if (this.name == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeString(this.name);
    }

    if (this.hasHumanPet == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      out.writeBoolean(this.hasHumanPet);
    }

    if (this.species == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      this.species.customEncode(out);
    }

    if (this.roleNames == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      long size0 = this.roleNames.size();
      out.writeArrayStart();
      out.setItemCount(size0);
      long actualSize0 = 0;
      for (java.lang.CharSequence e0: this.roleNames) {
        actualSize0++;
        out.startItem();
        out.writeString(e0);
      }
      out.writeArrayEnd();
      if (actualSize0 != size0)
        throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");
    }

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.eventId = in.readString(this.eventId instanceof Utf8 ? (Utf8)this.eventId : null);

      this.timestamp = in.readString(this.timestamp instanceof Utf8 ? (Utf8)this.timestamp : null);

      this.source = in.readString(this.source instanceof Utf8 ? (Utf8)this.source : null);

      this.id = in.readString(this.id instanceof Utf8 ? (Utf8)this.id : null);

      if (in.readIndex() != 1) {
        in.readNull();
        this.name = null;
      } else {
        this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.hasHumanPet = null;
      } else {
        this.hasHumanPet = in.readBoolean();
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.species = null;
      } else {
        if (this.species == null) {
          this.species = new com.tomasgimenez.animalia.avro.Species();
        }
        this.species.customDecode(in);
      }

      if (in.readIndex() != 1) {
        in.readNull();
        this.roleNames = null;
      } else {
        long size0 = in.readArrayStart();
        java.util.List<java.lang.CharSequence> a0 = this.roleNames;
        if (a0 == null) {
          a0 = new SpecificData.Array<java.lang.CharSequence>((int)size0, SCHEMA$.getField("roleNames").schema().getTypes().get(1));
          this.roleNames = a0;
        } else a0.clear();
        SpecificData.Array<java.lang.CharSequence> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a0 : null);
        for ( ; 0 < size0; size0 = in.arrayNext()) {
          for ( ; size0 != 0; size0--) {
            java.lang.CharSequence e0 = (ga0 != null ? ga0.peek() : null);
            e0 = in.readString(e0 instanceof Utf8 ? (Utf8)e0 : null);
            a0.add(e0);
          }
        }
      }

    } else {
      for (int i = 0; i < 8; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.eventId = in.readString(this.eventId instanceof Utf8 ? (Utf8)this.eventId : null);
          break;

        case 1:
          this.timestamp = in.readString(this.timestamp instanceof Utf8 ? (Utf8)this.timestamp : null);
          break;

        case 2:
          this.source = in.readString(this.source instanceof Utf8 ? (Utf8)this.source : null);
          break;

        case 3:
          this.id = in.readString(this.id instanceof Utf8 ? (Utf8)this.id : null);
          break;

        case 4:
          if (in.readIndex() != 1) {
            in.readNull();
            this.name = null;
          } else {
            this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          }
          break;

        case 5:
          if (in.readIndex() != 1) {
            in.readNull();
            this.hasHumanPet = null;
          } else {
            this.hasHumanPet = in.readBoolean();
          }
          break;

        case 6:
          if (in.readIndex() != 1) {
            in.readNull();
            this.species = null;
          } else {
            if (this.species == null) {
              this.species = new com.tomasgimenez.animalia.avro.Species();
            }
            this.species.customDecode(in);
          }
          break;

        case 7:
          if (in.readIndex() != 1) {
            in.readNull();
            this.roleNames = null;
          } else {
            long size0 = in.readArrayStart();
            java.util.List<java.lang.CharSequence> a0 = this.roleNames;
            if (a0 == null) {
              a0 = new SpecificData.Array<java.lang.CharSequence>((int)size0, SCHEMA$.getField("roleNames").schema().getTypes().get(1));
              this.roleNames = a0;
            } else a0.clear();
            SpecificData.Array<java.lang.CharSequence> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<java.lang.CharSequence>)a0 : null);
            for ( ; 0 < size0; size0 = in.arrayNext()) {
              for ( ; size0 != 0; size0--) {
                java.lang.CharSequence e0 = (ga0 != null ? ga0.peek() : null);
                e0 = in.readString(e0 instanceof Utf8 ? (Utf8)e0 : null);
                a0.add(e0);
              }
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










