$(function() {
    "use strict";

    var transformValue = function (val) {
        return {
            "x": val.t0,
            "y": val.t1 - val.t0
        };
    };

    var orderValue = function (a, b) {
        return a.x - b.x;
    };

    var transformSites = function (sites) {
        var ret = [];
        sites.forEach(function (site) {
            ret.push({ "key": "GET " + site.name, "values": site.gets.map(transformValue).sort(orderValue) },
                { key: "POST " + site.name, values: site.posts.map(transformValue).sort(orderValue) })
        });
        return ret;
    };

    d3.json("sites", function (data) {
        nv.addGraph(function () {
            var chart = nv.models.lineWithFocusChart().interpolate("monotone").useInteractiveGuideline(true);
            chart.xAxis.tickFormat(function(d) { return d3.time.format("%a %H:%M")(new Date(d)); });
            chart.x2Axis.tickFormat(function(d) { return d3.time.format("%a %H:%M")(new Date(d)); });
            chart.yAxis.axisLabel("Milliseconds").tickFormat(d3.format(".r"));
            d3.select("#chart svg").datum(transformSites(data.sites)).call(chart);
            nv.utils.windowResize(chart.update);
            return chart;
        });
    });
});
