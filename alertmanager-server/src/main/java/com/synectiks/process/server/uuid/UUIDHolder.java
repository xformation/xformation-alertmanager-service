package com.synectiks.process.server.uuid;

/**
* com/eaio/uuid/UUIDHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from uuid.idl
* Sonntag, 7. März 2004 21.35 Uhr CET
*/


/**
 * The UUID struct.
 */
public final class UUIDHolder implements org.omg.CORBA.portable.Streamable
{
  public com.synectiks.process.server.uuid.UUID value = null;

  public UUIDHolder ()
  {
  }

  public UUIDHolder (com.synectiks.process.server.uuid.UUID initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = com.synectiks.process.server.uuid.UUIDHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    com.synectiks.process.server.uuid.UUIDHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return com.synectiks.process.server.uuid.UUIDHelper.type ();
  }

}
