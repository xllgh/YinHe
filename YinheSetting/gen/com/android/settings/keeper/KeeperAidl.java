/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\svn\\YinheSetting\\src\\com\\android\\settings\\keeper\\KeeperAidl.aidl
 */
package com.android.settings.keeper;
public interface KeeperAidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.android.settings.keeper.KeeperAidl
{
private static final java.lang.String DESCRIPTOR = "com.android.settings.keeper.KeeperAidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.android.settings.keeper.KeeperAidl interface,
 * generating a proxy if needed.
 */
public static com.android.settings.keeper.KeeperAidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.android.settings.keeper.KeeperAidl))) {
return ((com.android.settings.keeper.KeeperAidl)iin);
}
return new com.android.settings.keeper.KeeperAidl.Stub.Proxy(obj);
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
case TRANSACTION_getPPPOEUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPPPOEUserName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setPPPOEUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setPPPOEUserName(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPPPOEPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPPPOEPasswd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setPPPOEPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setPPPOEPasswd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDHCPUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDHCPUserName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setDHCPUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setDHCPUserName(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDHCPPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getDHCPPasswd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setDHCPPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setDHCPPasswd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVAuthUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVAuthUrl();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVAuthUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVAuthUrl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVReserveAuthUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVReserveAuthUrl();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVReserveAuthUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVReserveAuthUrl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVLogUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVLogUrl();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVLogUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVLogUrl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVUserName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVUserName(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVPasswd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVPasswd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getAccessType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAccessType();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setAccessType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setAccessType(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getAccessMethod:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getAccessMethod();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setAccessMethod:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setAccessMethod(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVWGUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVWGUrl();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVWGUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVWGUrl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVWGUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVWGUserName();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVWGUserName:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVWGUserName(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVWGPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVWGPasswd();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVWGPasswd:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVWGPasswd(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVUpgradeUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVUpgradeUrl();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVUpgradeUrl:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVUpgradeUrl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getParam:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getParam(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setParam:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.setParam(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getITVLogInterval:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getITVLogInterval();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_setITVLogInterval:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.setITVLogInterval(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.android.settings.keeper.KeeperAidl
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
@Override public java.lang.String getPPPOEUserName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPPPOEUserName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setPPPOEUserName(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setPPPOEUserName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getPPPOEPasswd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPPPOEPasswd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setPPPOEPasswd(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setPPPOEPasswd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getDHCPUserName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDHCPUserName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDHCPUserName(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setDHCPUserName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getDHCPPasswd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDHCPPasswd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setDHCPPasswd(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setDHCPPasswd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVAuthUrl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVAuthUrl, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVAuthUrl(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVAuthUrl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVReserveAuthUrl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVReserveAuthUrl, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVReserveAuthUrl(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVReserveAuthUrl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVLogUrl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVLogUrl, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVLogUrl(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVLogUrl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVUserName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVUserName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVUserName(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVUserName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVPasswd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVPasswd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVPasswd(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVPasswd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getAccessType() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAccessType, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setAccessType(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setAccessType, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getAccessMethod() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAccessMethod, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setAccessMethod(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setAccessMethod, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVWGUrl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVWGUrl, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVWGUrl(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVWGUrl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVWGUserName() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVWGUserName, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVWGUserName(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVWGUserName, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVWGPasswd() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVWGPasswd, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVWGPasswd(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVWGPasswd, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVUpgradeUrl() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVUpgradeUrl, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVUpgradeUrl(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVUpgradeUrl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getParam(java.lang.String key) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
mRemote.transact(Stub.TRANSACTION_getParam, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setParam(java.lang.String key, java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(key);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setParam, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String getITVLogInterval() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getITVLogInterval, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setITVLogInterval(java.lang.String value) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(value);
mRemote.transact(Stub.TRANSACTION_setITVLogInterval, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getPPPOEUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_setPPPOEUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getPPPOEPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setPPPOEPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getDHCPUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setDHCPUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getDHCPPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setDHCPPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getITVAuthUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setITVAuthUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_getITVReserveAuthUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_setITVReserveAuthUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_getITVLogUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_setITVLogUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_getITVUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_setITVUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_getITVPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_setITVPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_getAccessType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_setAccessType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_getAccessMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_setAccessMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_getITVWGUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_setITVWGUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_getITVWGUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_setITVWGUserName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_getITVWGPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_setITVWGPasswd = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_getITVUpgradeUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_setITVUpgradeUrl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_getParam = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_setParam = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_getITVLogInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_setITVLogInterval = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
}
public java.lang.String getPPPOEUserName() throws android.os.RemoteException;
public void setPPPOEUserName(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getPPPOEPasswd() throws android.os.RemoteException;
public void setPPPOEPasswd(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getDHCPUserName() throws android.os.RemoteException;
public void setDHCPUserName(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getDHCPPasswd() throws android.os.RemoteException;
public void setDHCPPasswd(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVAuthUrl() throws android.os.RemoteException;
public void setITVAuthUrl(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVReserveAuthUrl() throws android.os.RemoteException;
public void setITVReserveAuthUrl(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVLogUrl() throws android.os.RemoteException;
public void setITVLogUrl(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVUserName() throws android.os.RemoteException;
public void setITVUserName(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVPasswd() throws android.os.RemoteException;
public void setITVPasswd(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getAccessType() throws android.os.RemoteException;
public void setAccessType(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getAccessMethod() throws android.os.RemoteException;
public void setAccessMethod(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVWGUrl() throws android.os.RemoteException;
public void setITVWGUrl(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVWGUserName() throws android.os.RemoteException;
public void setITVWGUserName(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVWGPasswd() throws android.os.RemoteException;
public void setITVWGPasswd(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVUpgradeUrl() throws android.os.RemoteException;
public void setITVUpgradeUrl(java.lang.String value) throws android.os.RemoteException;
public java.lang.String getParam(java.lang.String key) throws android.os.RemoteException;
public void setParam(java.lang.String key, java.lang.String value) throws android.os.RemoteException;
public java.lang.String getITVLogInterval() throws android.os.RemoteException;
public void setITVLogInterval(java.lang.String value) throws android.os.RemoteException;
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   