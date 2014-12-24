var app = angular.module('myApp', ["mobile-angular-ui", "ngRoute"]);

var partial_dir = "assets/partials/";

app.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/schedule/:weekDate', {
                templateUrl: partial_dir + 'schedule.html',
                controller: 'ScheduleCtrl'
            }).
            when('/schedule', {
                templateUrl: partial_dir + 'schedule.html',
                controller: 'ScheduleCtrl'
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

app.controller('ScheduleCtrl', ['$scope', '$http', '$routeParams',
    function ($scope, $http, $routeParams) {
        var weekDate = $routeParams.weekDate;
        if (Date.parse(weekDate)) {
            weekDate = new Date(weekDate);
        } else {
            weekDate = new Date();
        }

        if (weekDate.getDay() > 0) {
            weekDate = dateUtil.addDays(weekDate, 7 - weekDate.getDay());
        }

        var addDays = function (currentDate, daysToAdd) {
            var newDate = currentDate;

        }

        var week = {
            date: weekDate,
            fast_sunday: true,
            nextWeek: function () {
//                week.fast_sunday = !week.fast_sunday;
                return dateUtil.addDays(weekDate, 7);
            },
            previousWeek: function () {
                return dateUtil.addDays(weekDate, -7);
            }
        }
        $scope.week = week;

        $scope.members = [
            {name: "Trevor Allred"}
        ];
    }]);

var dateUtil = {
    addDays: function (oldDate, daysToAdd) {
        var newDate = new Date(oldDate);
        newDate.setDate(newDate.getDate() + daysToAdd);
        return newDate;
    }
}