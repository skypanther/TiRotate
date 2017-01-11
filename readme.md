
TiRotate - Android Image Rotation Module
===========================================

Is it a Titanium bug or an Android bug or a handset-maker bug? Who cares. Sometimes you take a photo with your phone upright (portrait) and it ends up sideways. This module is meant to fix that.

The TiRotate module will rotate an image to an orientation you specify and set its EXIF orientation tag to reflect that orientation (so that web clients, etc. will respect the new orientation).

Note: *This module doesn't support free rotation. You can rotate to any of the cardinal coordinates -- portrait, landscape right, upside-down, landscape left.*


Obtaining / Installing
-------------

### Recommended

```shell
$ gittio install com.skypanther.tirotate
```

###Manual method (works, but why not use gittio?):

1. Download the zip from the dist folder
2. Unzip to your app's modules/android folder
3. Add the module to the modules tag:

```xml
<modules>
	<module platform="android">com.skypanther.tirotate</module>
</modules>
```

Using
-------------

In your controller, instantiate it and use it:

```
var tirotate = require('com.skypanther.tirotate');
var file = Ti.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'photo.jpg');
var orientation = tirotate.getExifOrientation(file.nativePath);
if (orientation === tirotate.ORIENTATION_NORMAL) {
	alert("The image is portrait");
}
```

### Methods

| Method | Description |
|--------|-------------|
|`getExifOrientationDegrees(path)` | Get the image's EXIF orientation in degrees | 
|`getExifOrientation(path)` | Get the image's EXIF orientation tag value | 
|`setExifOrientation(path, targetOrientation)` | Set the image's EXIF orientation tag | 
|`rotate(path, targetOrientation)` |  Rotates the image to specified orientation *and* updates its EXIF tag |

#### Examples:

**Get the orientation of an image file in degrees**

```
tirotate.getExifOrientationDegrees(file.nativePath); // e.g. 90
```

**Get the EXIF orientation tag value**

```
tirotate.getExifOrientation(file.nativePath); // 1 (ORIENTATION_NORMAL)
```
(The resulting value will match the constants defined by the native ExifInterface. See [https://developer.android.com/reference/android/media/ExifInterface.html](https://developer.android.com/reference/android/media/ExifInterface.html) for those values.)


**Set the EXIF orientation tag value**

```
tirotate.setExifOrientation(file.nativePath, tirotate.PORTRAIT);
```

Alternatively:

```
tirotate.setExifOrientation(file.nativePath, tirotate.ORIENTATION_NORMAL);
```

**Rotate the image AND set its EXIF tag to the new orientation**

```
if (tirotate.rotate(file.nativePath, tirotate.PORTRAIT)) {
	// successfully rotated
} else {
	// failed to rotate
}
```

Alternatively:


```
if (tirotate.rotate(file.nativePath, tirotate.ORIENTATION_NORMAL)) {
	// successfully rotated
} else {
	// failed to rotate
}
```

### Constants

The module defines a few constants that you should use when interacting with images via the module's methods. For example, assuming you instantiate the module in a variable `tirotate` you would reference the EXIF portrait orientation as `tirotate.ORIENTATION_NORMAL`

|Constant|Description|Value|
|---|---|---|
| `DEGREES_PORTRAIT` | Integer rotation, used when reading the EXIF orientation of an image | 0 |
| `DEGREES_LANDSCAPE_RIGHT` | Integer rotation, used when reading the EXIF orientation of an image | 90 |
| `DEGREES_UPSIDE_PORTRAIT` | Integer rotation, used when reading the EXIF orientation of an image | 180 |
| `DEGREES_LANDSCAPE_LEFT` | Integer rotation, used when reading the EXIF orientation of an image | 270 |
| `PORTRAIT` | String used when rotating an image to a specific orientation | "portrait" |
| `LANDSCAPE_RIGHT` | String used when rotating an image to a specific orientation | "landscape_right" |
| `UPSIDE_PORTRAIT` | String used when rotating an image to a specific orientation | "upside_portrait" |
| `LANDSCAPE_LEFT` | String used when rotating an image to a specific orientation | "landscape_left" |
| `ORIENTATION_NORMAL` | Corresponds to the EXIF orientation constant for the portrait orientation | ExifInterface.ORIENTATION_NORMAL |
| `ORIENTATION_ROTATE_90` | Corresponds to the EXIF orientation constant for the landscape right orientation | ExifInterface.ORIENTATION_ROTATE_90 |
| `ORIENTATION_ROTATE_180` | Corresponds to the EXIF orientation constant for the upside-down orientation | ExifInterface.ORIENTATION_ROTATE_180 |
| `ORIENTATION_ROTATE_270` | Corresponds to the EXIF orientation constant for the landscape left orientation | ExifInterface.ORIENTATION_ROTATE_270 |
| `ORIENTATION_UNDEFINED` | Corresponds to the EXIF orientation constant for the undefined orientation | ExifInterface.ORIENTATION_UNDEFINED |


Contributing
-------------------

I welcome any and all help, naturally. I could specifically use testing on various Android devices. Open issues, or even better, submit a pull request.

License & Copyright
-------------------------

Copyright &copy; 2017 Tim Poulsen distributed under the MIT license.