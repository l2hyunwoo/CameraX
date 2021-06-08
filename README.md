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

### 생명주기와 연관시킬 수 있는 ProcessCameraProvider 객체를 반환시킨다

ProcessCameraProvider 객체의 역할은 Camera의 생명주기를 Activity와 같은 LifeCycleOwner의 생명주기에 Binding 시키는 것이다.

```kotlin
val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
```

### ListenableFuture에 Listener 달기

> The listener will run when the Future's computation is complete or,
> if the computation is already complete, immediately.

```kotlin
fun ListenableFuture.addListener(runnable: Runnable, executor: Executor)
```

executor 스레드에서 실행할 행동(runnable)을 정의한다. 여기서 카메라는 메인 스레드에서 실행을 해야하니 ``ContextCompat.getMainExecutor(this)``로 받아온다.

### 생명주기에 binding할 수 있는 ProcessCameraProvider 객체 가져오기

```kotlin
val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
```

### Set PreviewView, CameraSelector

#### PreviewView

카메라 미리보기 화면을 구현해주는 것, Builder 패턴을 통해 구현

#### CameraSelector

디폴트 카메라 세팅, 디폴트는 후면 카메라(DEFAUlT_BACK_CAMERA)

```kotlin
val preview = Preview.Builder()
    .build()
    .also { it.setSurfaceProvider(binding.previewMain.surfaceProvider) }
```

여기서 잠깐, setSurfaceProvider(previewView: PreviewView) 함수는 PreviewView에 SurfaceProvider를 제공해주는 역할을 맡는다.<br/>
SurfaceProvider는 데이터(이미지) 데이터를 받을 준비가 되었다는 신호를 카메라에게 보내주는 역할을 맡는다. 매개변수 중에서 Executor가 따로 설정되어있지 않으면 Main Thread에서 SurfaceProvider를 제공한다.
만약 null 값으로 Provider를 제거하면 카메라는 Preview 객체에서 이미지를 만드는 것을 멈춘다.

### 생명주기에 binding 시키기

```kotlin
    runCatching {
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview)
    }.onFailure { Log.e(TAG, "Use case binding failed", it) }
```

## Implement ImageCapture use case

이제 사진 찍는 것까지 구현을 하려면 ImageCapture 역시 구현해야한다. startCamera에서도 이 use case를 만들어야 사진 찍는 기능을 구현할 수 있기 때문에 ``takePhoto``, ``startCamera`` 두 함수를 모두 건드려야한다.

```kotlin
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // 사진 저장 장소
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )

        // outputFile의 Configuration을 담당
        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                // 사진을 찍고 어떻게 저장할 지에 대한 구현부
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val message = "Photo capture succeeded: $savedUri"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, message)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    // startCamera()

    // 이미지 캡쳐하는 use case를 빌더패턴을 통해 구현한다.
    imageCapture = ImageCapture.Builder().build()
```
