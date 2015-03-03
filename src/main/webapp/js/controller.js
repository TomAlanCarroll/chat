var chatApp = angular.module("Chat", ['ngMap']);

chatApp.controller("ChatController", function ($scope, $http) {
    var params = {};

    function getQueryVariable(variable) {
        var query = window.location.search.substring(1);
        var vars = query.split("&");
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            if (pair[0] == variable) {
                return pair[1];
            }
        }
        return (false);
    }

    if (getQueryVariable("id")) {
        params.id = getQueryVariable("id");
    }

    $http.get("request", {params: params}).success(
        function (response) {
            var dateRegex = /, 20\d{2} (.*?)$/;
            $scope.chats = response;

            // Convert the date strings to times in the transcripts
            angular.forEach($scope.chats, function (value, key) {
                angular.forEach(value.transcripts, function (value, key) {
                    if (value.date) {
                        try {
                            value.formatted_date = dateRegex.exec(value.date)[1];
                        } catch (e) {
                            // do nothing...
                        }
                    }
                }, value);
            }, $scope.chats);
        }
    );
});
