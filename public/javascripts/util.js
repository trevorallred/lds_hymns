var util = angular.module('util', []);

var agendaItems = {
    song: {
        opening: "Opening Hymn",
        sacrament: "Sacrament Hymn",
        rest: "Rest Hymn",
        closing: "Closing Hymn"
    },
    prayer: {
        invocation: "Invocation",
        benediction: "Benediction"
    },
    speaker: {
        youth: "Youth Speaker",
        first: "First Speaker",
        second: "Second Speaker",
        third: "Third Speaker",
        fourth: "Fourth Speaker",
        final: "Concluding Speaker"
    },
    other: {
        special: "Special Number",
        guest: "Guest Speaker"
    },
    finalSpeaker: {title: " Speaker", type: "talk", assignment: "member"}
};

var meetingFormat = [
    ["song/opening", "prayer/invocation", "song/sacrament", "speaker/first", "speaker/second", "song/rest", "speaker/final", "song/closing", "prayer/benediction"]
];

/* The 'gender' filter. */
util.filter('songType', function () {
    return function(type) {
        return agendaItems.song[type] || type;
    };
});

util.factory('psNotify', function ($rootScope, $timeout) {
    $rootScope.warning = null;
    $rootScope.error = null;

    return {
        showWarning: function (msg) {
            if (msg) {
                $rootScope.warning = msg;
                $timeout(function () {
                    $rootScope.warning = null
                }, 3000);
            }
        },
        showError: function (msg) {
            if (msg) {
                $rootScope.error = msg;
                $timeout(function () {
                    $rootScope.error = null
                }, 3000);
            }
        }
    }
});

util.factory('psHttp', function ($http, psNotify) {
    function handleError(errorData) {
        psNotify.showError(errorData.error);
    }

    return {
        get: function (url, successCallback) {
            $http.get(url).success(successCallback).error(handleError);
        },
        post: function (url, data, successCallback) {
            $http.post(url, data).success(successCallback).error(handleError);
        }
    }
});

util.factory('commonDates', function () {
    var self = {
        nextSunday: function (weekDate) {
            if (Date.parse(weekDate)) {
                weekDate = new Date(weekDate);
            } else {
                weekDate = new Date();
            }

            if (weekDate.getDay() > 0) {
                weekDate = self.addDays(weekDate, 7 - weekDate.getDay());
            }
            return weekDate;
        },
        addDays: function (oldDate, daysToAdd) {
            var newDate = new Date(oldDate);
            newDate.setDate(newDate.getDate() + daysToAdd);
            return newDate;
        }
    };
    return self;
});

