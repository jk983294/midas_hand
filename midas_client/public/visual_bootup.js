(function() {
    /**
     * check if it is develop environment
     * for develop environment, it will load uncompressed version lib
     * for production, it will load compressed version lib.
     */
    function isDevEnv() {
        var scripts = document.getElementsByTagName('script'),
            localhostTests = [
                /^localhost$/,
                /\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(:\d{1,5})?\b/ // IP v4
            ],
            host = window.location.hostname,
            isDevelopment = null,
            queryString = window.location.search,
            test, path, i, ln, scriptSrc, match;

        for (var i = 0, ln = scripts.length; i < ln; i++) {
            scriptSrc = scripts[i].src;
            match = scriptSrc.match(/bootup\.js$/);
            if (match) {
                path = scriptSrc.substring(0, scriptSrc.length - match[0].length);
                break;
            }
        }

        if (isDevelopment === null) {
            for (var i = 0, ln = localhostTests.length; i < ln; i++) {
                test = localhostTests[i];
                if (host.search(test) !== -1) {
                    isDevelopment = true;
                    break;
                }
            }
        }
        if (isDevelopment === null && window.location.protocol === 'file:') {
            isDevelopment = true;
        }
        //      isDevelopment = false;
        return {
            isdev: isDevelopment,
            path: path
        };
    }

    function loadJsLibs(jsLibs, pathBase, isdev) {
        for (var i = 0, ln = jsLibs.length; i < ln; i++) {
            document.write('<script type="text/javascript" src="' + pathBase + 'Scripts/' + ((isdev) ? 'dev/' : 'prod/') + jsLibs[i] + ((isdev) ? '' : '.min') + '.js"></script>');
        }
    }

    function loadJS(jsPath) {
        document.write('<script type="text/javascript" src="' + jsPath + '"></script>');
    }

    function loadCss(cssfiles) {
        var head = document.getElementsByTagName('head').item(0);
        for (var i = 0, ln = cssfiles.length; i < ln; i++) {
            var css = document.createElement('link');
            css.href = "Content/" + cssfiles[i] + ".css";
            css.rel = 'stylesheet';
            css.type = 'text/css';
            head.appendChild(css);
        }
    }



    //javascript lib and css file list to load
    var jsLibs = [
            "jquery-1.11.1", "bootstrap",
            "angular", "angular-route", "angular-animate", "ng-table", "angular-resource",
            "ui-bootstrap-tpls-0.11.2", "select",
            "jquery.flot", "jquery.flot.time", "jquery.flot.tooltip_0.5",
            "d3", "nv.d3", "angular-nvd3", "ui-grid", "echarts.3.8.4"
        ],
        cssFiles = [
            "bootstrap.min", "bootstrap-theme.min", "ng-table.min", "select.min", "nv.d3.min", "mytheme", "ui-grid.min"
        ];

    var initConfig = isDevEnv();
    loadJsLibs(jsLibs, "", initConfig.isdev);
    loadCss(cssFiles);

    /**
     * load user defined  module
     * load sequence doesn't matter, all controller just define functions,
     * every controller should register to MidasController in indexController.js
     * it is midas.js job to trigger all initialization by call MidasController's onReady function
     */
    loadJS("app/directives/FormatFilter.js");
    loadJS("app/directives/FlotChart.js");
    loadJS("visual_app/app.js");
    //load services
    loadJS("app/services/UtilService.js");
    loadJS("app/services/StockUtils.js");
    loadJS("visual_app/services/VisualDataService.js");
    //load controller
    loadJS("visual_app/controllers/chartController.js");
    loadJS("visual_app/controllers/tableController.js");
    loadJS("visual_app/controllers/candleController.js");
    loadJS("visual_app/controllers/lineController.js");
})();
