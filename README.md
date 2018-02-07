# Android.Track
### 1、跨进程数据共享遇到的吭
##### 1.1 SharedPreferences 数据同步问题，MODE_MULTI_PROCESS 当多个进程同时而又高频的调用commit方法时，就会导致文件被反复覆盖写入，而并没有被及时读取，所以造成进程间数据的不同步,https://www.jianshu.com/p/bdebf741221e
##### 1.2 跨进程所有的数据通过ContentProvider共享
