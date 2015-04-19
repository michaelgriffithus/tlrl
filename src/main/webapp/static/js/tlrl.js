'use strict';

/** Creates TLRL specific services */
angular.module('tlrlServices', ['ngResource'])
	/** Service for managing Bookmarks. */
	.factory('bookmarkService', ['$resource', function($resource) {
		return $resource('/api/:user/urls/:id', {}, {
			'update': {method: 'PUT'},
			'updateStatus': {method: 'PUT', url: '/api/urls/:id/status'},
			'search': {method: 'GET', url: '/api/:user/search', isArray: false},
			'tags': {method: 'GET', url: '/api/tags', isArray: false},
			'query': {method: 'GET', isArray: false},
			'recent': {method: 'GET', url: '/api/recent', isArray: false},
			'popular': {method: 'GET', url: '/api/popular', isArray: false},
			'webpage': {method: 'GET', url: '/api/url/:id', isArray: false}
		});
	}])
	
angular.module('tlrlApp', [  
		'tlrlServices',
		'ngRoute',
		'ngSanitize'
 ])
 .config(['$routeProvider', '$locationProvider', '$httpProvider',
    function($routeProvider, $locationProvider, $httpProvider) {
	 		$routeProvider
	 			.when('', {
	 				templateUrl: '',
	 				controller: ''
 			});
	 		
	 		$locationProvider.html5Mode({
 				enabled: true,
 				requireBase: false
	 		});
 }])
