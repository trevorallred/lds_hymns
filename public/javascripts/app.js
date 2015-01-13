var app = angular.module('myApp', ["mobile-angular-ui", "ngRoute", "util"]);

var partial_dir = "assets/partials/";

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/schedule', {
                templateUrl: partial_dir + 'schedule.html',
                controller: 'ScheduleCtrl'
            }).
            when('/schedule/:weekDate', {
                templateUrl: partial_dir + 'schedule.html',
                controller: 'ScheduleCtrl'
            }).
            when('/schedule/:weekDate/song/:songType', {
                templateUrl: partial_dir + 'choose_song.html',
                controller: 'SelectHymnCtrl'
            }).
            when('/schedule/:weekDate/speaker/:memberType', {
                templateUrl: partial_dir + 'choose_member.html',
                controller: 'SelectMemberCtrl'
            }).
            when('/schedule/:weekDate/prayer/:memberType', {
                templateUrl: partial_dir + 'choose_member.html',
                controller: 'SelectMemberCtrl'
            }).
            when('/about', {
                templateUrl: partial_dir + 'about.html'
            }).
            when('/todo', {
                templateUrl: partial_dir + 'todo.html'
            }).
            otherwise({
                redirectTo: '/todo'
            });
    }]);

app.controller('ScheduleCtrl', ['$scope', '$http', '$routeParams', '$filter', 'commonDates',
    function ($scope, $http, $routeParams, $filter, commonDates) {
        var weekDate = commonDates.nextSunday($routeParams.weekDate);

        $scope.week = {
            date: weekDate,
            label: $filter('date')(weekDate, 'yyyy-MM-dd'),
            fast_sunday: true,
            nextWeek: function () {
                return commonDates.addDays(weekDate, 7);
            },
            previousWeek: function () {
                return commonDates.addDays(weekDate, -7);
            }
        }

        $scope.members = [
            {name: "Trevor Allred"}
        ];
    }]);

app.controller('SelectHymnCtrl', ['$scope', '$http', '$routeParams',
    function ($scope, $http, $routeParams) {
        $scope.songType = $routeParams.songType;
        var view = $routeParams.view;
        $scope.orderBy = "number";

        $http.get('/assets/hymns.json').
            success(function(data, status, headers, config) {
                $scope.hymns = data;
            }).
            error(function(data, status, headers, config) {
                $scope.hymns = [
                    {"number": 0, "name": "ERROR: Failed to load hymns"}
                ];
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            });
    }]);

app.controller('SelectMemberCtrl', ['$scope', '$http', '$routeParams',
    function ($scope, $http, $routeParams) {
        $scope.songType = $routeParams.memberType;
        var view = $routeParams.view;

        $scope.members = [
            {name: "Trevor Allred"}
        ];
    }]);
