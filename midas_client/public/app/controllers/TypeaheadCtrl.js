/**
 * control typeahead
 */
midasApp.controller('TypeaheadCtrl', function($scope, MidasData, Utils, StockUtils, $location) {
    /*** typeahead data */
    $scope.typeahead = {
        allstocks : [],
        queryStr : '',
        loadingTips : {}
    };

    $scope.getTips = function (queryStr) {
        return MidasData.getTips(queryStr).$promise.then(function(response){
            return response.tips.map(function(item){
                return item;
            });
        });
    };

    $scope.getAction = function (queryStr) {
        MidasData.getAction(queryStr).$promise.then(function(response){
            if(response.status === "FAIL"){
                MidasData.addAlert({type : "danger", msg : response.description});
            } else if(response.status === "SUCCESS"){
                MidasData.addAlert({type : "success", msg : response.description});
            }
            actionHandler(response.action);
        });
    };

    $scope.inputKeyPress = function(event){
        if(event.which === 13){
            //console.log("enter pressed", $scope.typeahead.queryStr);
        }
    }

    function actionHandler(action){
        if(!Utils.isNull(action)){
            var actions = action.split(" ");
            //console.log("actions", actions);

            /*** one parameter action */
            if(actions.length === 1 ){
                var actionStr = actions[0];
                if(StockUtils.isStockCode(actionStr)){
                    $location.path( "/Index/" + actionStr );
                } else if(actionStr === "task"){
                    $location.path( "/Task" );
                }
            } else if(actions.length > 1 ){
                if(StockUtils.isStockCode(actions)){
                    console.log("to cmp",  "/Comparison/" + action);
                    $location.path( "/Comparison/" + action );
                }
            }
        }
    }
});