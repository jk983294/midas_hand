var express = require('express');
var app = express();

// set the static files location /public/img will be /img for users
app.use(express.static(__dirname + '/public'));

var port = 8766;
app.listen(port);
console.log("server running at\n  => http://localhost:" + port + "/");

app.get('*', function(req, res) {
    res.sendfile('./public/visual_index.html');
});
