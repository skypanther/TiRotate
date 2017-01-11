var file = Titanium.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'testphoto.jpg');

var win = Ti.UI.createWindow({
	// open a single window
	backgroundColor: 'white'
});
var label = Ti.UI.createLabel({
	top: 20,
	color: "black"
});
win.add(label);

var img = Ti.UI.createImageView({
	bottom: 0,
	height: 300,
	width: Ti.UI.SIZE,
	image: file.nativePath
});

img.addEventListener('click', rotateImage);

win.add(img);

var tirotate = require('com.skypanther.tirotate');

function rotateImage() {
	var file = Titanium.Filesystem.getFile(Ti.Filesystem.applicationDataDirectory, 'testphoto.jpg');
	if (tirotate.rotate(file.nativePath, tirotate.PORTRAIT)) {
		img.image = file.read();
		label.text = tirotate.getExifOrientation(file.nativePath);
	} else {
		alert("Unable to rotate the image");
	}
}

win.open();