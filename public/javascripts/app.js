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
        $scope.orderBy = "title";

        $scope.hymns = [
            {page: 1, title: "The Morning Breaks"},
            {page: 2, title: "The Spirit of God"},
            {page: 3, title: "Redeemer of Israel"},
            {page: 242, title: "Called to Serve"}
        ];
    }]);

app.controller('SelectMemberCtrl', ['$scope', '$http', '$routeParams',
    function ($scope, $http, $routeParams) {
        $scope.songType = $routeParams.memberType;
        var view = $routeParams.view;

        $scope.members = [
            {name: "Trevor Allred"}
        ];
    }]);
