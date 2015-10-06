// set up ========================
var express  = require('express');
var app      = express();                               // create our app w/ express

// configuration =================


app.use(express.static(__dirname + '/public'));                 // set the static files location /public/img will be /img for users

var port = 8765;
// listen (start app with node server.js) ======================================
app.listen(port);
console.log("App listening on port " + port);


// application -------------------------------------------------------------
app.get('*', function(req, res) {
	res.sendfile('./public/index.html'); // load the single view file (angular will handle the page changes on the front-end)
});