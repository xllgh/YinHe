/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\Android\\androidproject\\AIDLServer\\src\\com\\example\\hellotest\\IRemoteService.aidl
 */
package com.example.hellotest;
public interface IRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.hellotest.IRemoteService
{
private static final java.lang.String DESCRIPTOR = "com.example.hellotest.IRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.hellotest.IRemoteService interface,
 * generating a proxy if needed.
 */
public static com.example.hellotest.IRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.hellotest.IRemoteService))) {
return ((com.example.hellotest.IRemoteService)iin);
}
return new com.example.hellotest.IRemoteService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPid:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPid();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getStudent:
{
data.enforceInterface(DESCRIPTOR);
com.example.hellotest.Student _result = this.getStudent();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setStudent:
{
data.enforceInterface(DESCRIPTOR);
com.example.hellotest.Student _arg0;
if ((0!=data.readInt())) {
_arg0 = com.example.hellotest.Student.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.setStudent(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_basicType:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
long _arg1;
_arg1 = data.readLong();
this.basicType(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setOnIRemoteServiceChangeListener:
{
data.enforceInterface(DESCRIPTOR);
com.example.hellotest.IRemoteListener _arg0;
_arg0 = com.example.hellotest.IRemoteListener.Stub.asInterface(data.readStrongBinder());
this.setOnIRemoteServiceChangeListener(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.hellotest.IRemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getPid() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPid, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public com.example.hellotest.Student getStudent() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.example.hellotest.Student _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStudent, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.example.hellotest.Student.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setStudent(com.example.hellotest.Student student) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((student!=null)) {
_data.writeInt(1);
student.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setStudent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void basicType(int tInt, long tLong) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(tInt);
_data.writeLong(tLong);
mRemote.transact(Stub.TRANSACTION_basicType, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setOnIRemoteServiceChangeListener(com.example.hellotest.IRemoteListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setOnIRemoteServiceChangeListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getPid = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getStudent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setStudent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_basicType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setOnIRemoteServiceChangeListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int getPid() throws android.os.RemoteException;
public com.example.hellotest.Student getStudent() throws android.os.RemoteException;
public void setStudent(com.example.hellotest.Student student) throws android.os.RemoteException;
public void basicType(int tInt, long tLong) throws android.os.RemoteException;
public void setOnIRemoteServiceChangeListener(com.example.hellotest.IRemoteListener listener) throws android.os.RemoteException;
}
