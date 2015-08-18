/*eslint-env node*/
"use strict";

var express = require('express');
var cfenv = require('cfenv');
var request = require('request');

var sites = [
    {
        "name": "Question 1: Factorials",
        "url": "http://dcq1.eu-gb.mybluemix.net/",
        "data": "1\r\n4\r\n3\r\n#",
        "expect": "<pre>1<br>24<br>6</pre>",
        "gets": [],
        "posts": []
    },
    {
        "name": "Question 2: Change",
        "url": "http://dcq2.eu-gb.mybluemix.net/",
        "data": "100,50,20,10,5,2,1:57\r\n100, 10, 50, 20, 5, 2, 1:36\r\n#",
        "expect": "<pre>50x1,5x1,2x1<br>20x1,10x1,5x1,1x1</pre>",
        "gets": [],
        "posts": []
    },
    {
        "name": "Question 3: Simultaneous equations",
        "url": "http://dcq3.eu-gb.mybluemix.net/",
        "data": "4x+3y=24\r\n5 x + y =19\r\n#\r\n2x+y=3\r\n3x-y=2\r\n#\r\n##",
        "expect": "<pre>x=3 y=4<br>x=1 y=1</pre>",
        "gets": [],
        "posts": []
    }
];

// Configuration values.
var CRON_INTERVAL = 30 * 60 * 1000; // 30 minutes
var CRON_LIMIT = 240; // 5 days

// Periodic background job maintains statistics.
var cron = function() {
    sites.forEach(function(site) {
        var t0 = Date.now();
        request({ "uri": site.url, "method": "GET" }, function(error, resp, body) {
            var t1 = Date.now();
            site.gets.push({ "t0": t0, "t1": t1, "statusCode": resp.statusCode, "expected": body.indexOf('<textarea') >= 0 });
            while (site.gets.length > CRON_LIMIT) { site.gets.shift(); }
        });
        t0 = Date.now();
        request({ "uri": site.url, "method": "POST", "form": { "data": site.data } }, function(error, resp, body) {
            var t1 = Date.now();
            site.posts.push({ "t0": t0, "t1": t1, "statusCode": resp.statusCode, "expected": body.indexOf(site.expect) >= 0 });
            while (site.posts.length > CRON_LIMIT) { site.posts.shift(); }
        });
    });
    setTimeout(cron, CRON_INTERVAL);
};
cron();

// Serve the static resources and a /sites RESTful service.
var app = express();
app.use(express.static(__dirname + '/public'));
app.get('/sites', function(req, res) { res.json({ "sites": sites }); });
var appEnv = cfenv.getAppEnv();
app.listen(appEnv.port, function() { console.log("Server listening on " + appEnv.url); });
