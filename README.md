# CameraX Study

## Perission Setting

```
// AndroidManifest.xml
<uses-feature android:name="android.hardware.camera.any" />
<uses-permission android:name="android.permission.CAMERA" />
```

- ``uses-feature``의 ``camera.any``는 전면 혹은 후면에 카메라가 있는 모든 기기를 대상으로 아래의 권한을 취득하겠다는 것이다
    - 만약 ``any``가 빠진다면, 후면에 카메라가 없는 기기는 대상에서 제외된다.
- 카메라 사용 권한을 취득하려면 ``<uses-permission android:name="android.permission.CAMERA" />``를 작성해야한다.

## Implementation Preview use case

CameraX에서는 **ViewFinder**가 촬영할 사진을 미리 볼 수 있는 역할을 함. 이는 Preview 클래스를 활용하여 구현이 가능함.

### ProcessCameraProvider 객체 생성

ProcessCameraProvider 객체의 역할은 Camera의 생명주기를 Activity와 같은 LifeCycleOwner의 생명주기에 Binding 시키는 것이다.

```kotlin
val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
```

### ProcessCameraProvider 객체에 Listener 달기

> The listener will run when the Future's computation is complete or,
> if the computation is already complete, immediately.


