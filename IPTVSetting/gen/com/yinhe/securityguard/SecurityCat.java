/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\DX_AWIFI\\IPTVSetting\\src\\com\\yinhe\\securityguard\\SecurityCat.aidl
 */
package com.yinhe.securityguard;
public interface SecurityCat extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yinhe.securityguard.SecurityCat
{
private static final java.lang.String DESCRIPTOR = "com.yinhe.securityguard.SecurityCat";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yinhe.securityguard.SecurityCat interface,
 * generating a proxy if needed.
 */
public static com.yinhe.securityguard.SecurityCat asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yinhe.securityguard.SecurityCat))) {
return ((com.yinhe.securityguard.SecurityCat)iin);
}
return new com.yinhe.securityguard.SecurityCat.Stub.Proxy(obj);
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
case TRANSACTION_startSecurityGuard:
{
data.enforceInterface(DESCRIPTOR);
this.startSecurityGuard();
reply.writeNoException();
return true;
}
case TRANSACTION_stopSecurityGuard:
{
data.enforceInterface(DESCRIPTOR);
this.stopSecurityGuard();
reply.writeNoException();
return true;
}
case TRANSACTION_startLocalCheck:
{
data.enforceInterface(DESCRIPTOR);
this.startLocalCheck();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yinhe.securityguard.SecurityCat
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
@Override public void startSecurityGuard() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startSecurityGuard, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopSecurityGuard() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopSecurityGuard, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startLocalCheck() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startLocalCheck, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startSecurityGuard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_stopSecurityGuard = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startLocalCheck = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void startSecurityGuard() throws android.os.RemoteException;
public void stopSecurityGuard() throws android.os.RemoteException;
public void startLocalCheck() throws android.os.RemoteException;
}
