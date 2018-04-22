// set up ========================
var express = require('express');
var app = express();

// configuration =================


app.use(express.static(__dirname + '/public')); // set the static files location /public/img will be /img for users

var port = 8765;
// listen (start app with node server.js) ======================================
app.listen(port);
console.log("server running at\n  => http://localhost:" + port + "/");


// application -------------------------------------------------------------
app.get('*', function(req, res) {
    res.sendfile('./public/midas_index.html');
});
