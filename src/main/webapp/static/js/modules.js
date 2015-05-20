'use strict';

/**
 * Service for interacting with TLRL's ReadLater/WebPage API.
 */
angular.module('tlrlServices', ['ngResource'])
.factory('TLRLService', ['$resource', function($resource) {
		return $resource('/api/:user/urls/:id', {}, {
			'update': {method: 'PUT', headers: _tlrlHeaders },
			'updateStatus': {method: 'PUT', url: '/api/urls/:id/status', headers: _tlrlHeaders },
			'search': {method: 'GET', url: '/api/:user/search', isArray: false},
			'tags': {method: 'GET', url: '/api/tags', isArray: false},
			'query': {method: 'GET', isArray: false},
			'recent': {method: 'GET', url: '/api/recent', isArray: false},
			'popular': {method: 'GET', url: '/api/popular', isArray: false},
			'popularDetails': {method: 'GET', url: '/api/urls/:id', isArray: false}
		});
}])
//.factory('securityInterceptor', ['$q', '$window', function ($q, $window) {
//	return {
//		'responseError': function(resp) {
//			if(resp.status === 401 || resp.status === 403) {
//				console.log("")
//			} 
//				//$window.location.href = '/signout';
//			return $q.reject(resp);
//		},
//		'response' : function(resp) {
//			return resp;
//		}
//	}
//}])
.factory('NavService', ['$location', '$route', '$window', function($location, $route, $window) {
	
	return {
		url: function(request) {
			$location.url($location.path() + '?p=' + request.page + 
					(request.term ? '&q=' + request.term : '') +
					(request.tags ? '&t=' + request.tags : '') +
					(request.filters ? '&f=' + request.filters : '') +
					(request.sort || '') 
				)
		},
		reload: function() {
			$route.reload();
		},
		redirect: function(path, local) {
			if(local) {
				$location.url(path);
			} else {
				$window.location.href = path;
			}
		}
	}
}]);

/**
 * Module loaded by application
 */
angular.module('tlrl', [
  'tlrlServices',
  'ngRoute',
  'ngSanitize'
])
.factory('Utils', [function() {
	return {
		isUrl: function (str) {
			var urlRegex = '^(?:(?:http|https|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$';
			var url = new RegExp(urlRegex, 'i');
			return str && str.length < 2083 && url.test(str);
		},
		flattenTags: function(tags) {
			var flattened = '';
			angular.forEach(tags, function(tag, i) {
				flattened += (i > 0 ? ', ' + tag.id : tag.id);
			})
			return flattened;
		},
		elevateTags: function(str) {
			var tags = []
			str = (str || "").trim()
			if(str.length > 0) {
				var tmp = str.split(/[|,:]+/);
				angular.forEach(tmp, function(value, i) {
					if(value.trim().length > 0) 
						tags.push({id: value});
				})
			}
			return tags;
		}
	}
}])
/**
 * Filters for this module
 */
.filter('htmlToPlaintext', function() {
    return function(text) {
    	// strips html from text using very basic regex
      return (text || '').replace(/<[^>]+>/gm, '');
    }
  })
.filter('dateFormat', function() {
	// formats date (given as timestamp) using moment.js "from now" style
	var threeMonthsAgo = moment().subtract(3, 'months');
	var today = moment();
	return function(ts) { 
		var dateCreated = moment(ts);
		if(dateCreated.isAfter(threeMonthsAgo)) {
			return dateCreated.fromNow();
		} else {
			return dateCreated.format('ll')
		}
	}
})
.config(['$routeProvider', '$locationProvider', '$httpProvider', 
    function($routeProvider, $locationProvider, $httpProvider) {
	//$httpProvider.interceptors.push('securityInterceptor');
	$locationProvider.html5Mode({
		enabled: true,
		requireBase: false
	});
	
	var listRoute = {
			templateUrl: '/partials/list.html',
			controller: 'urlsCtrl',
			reloadOnSearch: true
	}
	$routeProvider
		.when('/urls/:webUrlId', {
			templateUrl: '/partials/popular-details.html',
			controller: 'popularDetailsCtrl'
		})
		.when('/recent', {
			templateUrl: '/partials/recent.html',
			controller: 'recentCtrl',
			reloadOnSearch: true
		})
		.when("/popular", {
			templateUrl: '/partials/popular.html',
			controller: 'popularCtrl',
			reloadOnSearch: true
		})
		.when('/urls', listRoute)
		.when('/:user?/search', {
			templateUrl: '/partials/search.html',
			controller: 'searchCtrl',
			reloadOnSearch: true})
		.when('/:user', listRoute);
	
}])
/**
 * Base controller providing common functionality.
 */
.controller('baseCtrl', ['$scope', '$routeParams', '$location', '$filter', 'Utils', 'NavService', '$timeout', 'TLRLService', '$sce',
    function($scope, $routeParams, $location, $filter, Utils, NavService, $timeout, TLRLService, $sce) {

	$scope.newReadLaters = [];
	$scope.readLaters = [];
	
	$scope.pageRequest = {}
	$scope.pageResult = {}
	$scope.tags = {}
	
	$scope.isManageable = false;
	$scope.currentUser = currentUser;
	$scope.targetUser = null;
	
	$scope.setTargetUser = function() {
		if($routeParams.user) {
			$scope.targetUser = {
				name: $routeParams.user.substring(1),
				atName: $routeParams.user
			}
			$scope.isManageable = ($scope.currentUser && 
					($scope.currentUser.name === $scope.targetUser.name));
		} else {
			$scope.targetUser = null;
		}
	}
	
	$scope.getTargetUserAtName = function() {
		return $scope.targetUser ? $scope.targetUser.atName : '';
	}
	
	$scope.noOp = function() {}
	
	/** 
	 * Triggers a search by applying term onto location, where 
	 * listeners will see the location change and perform search. 
	 * This method makes it bookmark friendly.
	 */
	$scope.loadSearch = function(term) {
		$scope.loadSearchByUser(term, null);
	}
	
	$scope.loadSearchByUser = function(term, user) {
		//TODO: escape term?
		if(term) { $location.url((user ? ('/@' + user.name) : '') + '/search?q=' + term) }
	}
	
	/*
	 * Elevates/flattens ReadLater tags during save action. Used on both
	 * search and manage interfaces.
	 */
	function elevateAndNormalizeTags(readLater) {
		if(readLater.tagsFlattened) {
			readLater.tags = Utils.elevateTags(readLater.tagsFlattened);
		}
		angular.forEach(readLater.tags, function(tag, i) {
			readLater.tags[i] = {id: tag.id.trim()
				.replace(/\s+/g, '_') // whitespace to _
				.replace(/-/g, '_')		// replace _ with -
				.replace(/\W/g,'')		// remove non word chars
				.replace(/_/g, ' ')		// replace _ with -
				.trim()
				.replace(/ /g, '-')
				.replace(/-{2,}/g, '-')
				.toLowerCase()}; 
		});
		readLater.tagsFlattened = Utils.flattenTags(readLater.tags, "id");
	}
	
	$scope.isSearch = function() {
		return $location.path().indexOf('search') != -1;
	}
	$scope.path = function() {
		return $location.path();
	}
	$scope.isPopular = function() {
		return $location.path().indexOf('popular') != -1;
	}
	
	$scope.handleError = function(resp) {
		if(resp.status === 403 || resp.status === 401) {
			console.log("You must be signed in to perform that action!")
		}
	}
	
	$scope.addReadLater = function(url) {
		if(!Utils.isUrl(url)) {
			console.log("Not a valid url:", url);
			return;
		}
		TLRLService.save({}, {url: url}, function(readLater) {
			readLater.edit = true;
			$scope.newReadLaters.unshift(readLater);
			$scope.term = '';
		}, $scope.handleError);
	}
	
	/**
	 * Saves the given ReadLater. Called from either search or manage interfaces.
	 */
	$scope.saveReadLater = function (readLater, callback) {
		if(readLater && readLater.id) {
			// TODO: use md5 to check if dirty
			elevateAndNormalizeTags(readLater)
			if(readLater.tags.length > 5) {
				window.alert("You may have up to 5 tags max")
			} else {
				TLRLService.update({id: readLater.id}, {
						description: readLater.description,
						tags: readLater.tags,
						shared: readLater.shared,
						title: readLater.title
					}, function(saved) {
						handleOnSaveSuccess(saved, callback);
					} , 
					$scope.handleError
				);
			}
		}	
	}
	
	function handleOnSaveSuccess(saved, callback) {
		// we know it's add vs update, if our saved instance is in newReadLaters queue
		var wasAdd = false;
		angular.forEach($scope.newReadLaters, function(newReadLater, index) {
			if(newReadLater.url === saved.url) {
				wasAdd = true;
				$scope.newReadLaters.splice(index, 1);
			}
		});
		if(!wasAdd) {
			callback(saved);
		}
	}
	
	/**
	 * Get the parameter with given name. Takes optional
	 * default value (v) if name doesn't exists.
	 */
	$scope.getRouteParam = function(name, v) {
		return ($routeParams[name] && typeof($routeParams[name]) === 'string') ? 
			$routeParams[name] : (v || ""); 
	}
	
	$scope.buildPageRequest = function(page, sort) {
		if(angular.isArray(sort)) {
			// normalize sort, since it comes in as sort { property: .., direction: ..., }
			// from Spring and we haven't figured way to control serialization of PageIml
			var tmp = "";
			angular.forEach(sort, function(s, i) {
				tmp += '&s=' + s.property + ',' + angular.lowercase(s.direction)
			})
			// default is id,asc, hide at the moment until we get something friendlier
			sort = (tmp == '&s=id,asc' ? '' : tmp);
		}
		return { 
			page: (page || 0),
			sort: (sort || undefined),
			term: ($scope.getRouteParam('q') || undefined),
			tags: ($scope.getRouteParam('t') || undefined),
			filters: ($scope.getRouteParam('f') || undefined)
		}
	}
	
	/**
	 * Handles results returned from queries (queries are used 
	 * in manage view) and searches.
	 */
	$scope.handleResultPage = function(result) {
		$scope.readLaters = []; // important! clear out current list
		if(result.content) {
			angular.forEach(result.content, function(readLater, index) {
				// this should be moved to view directive
				readLater.tagsFlattened = Utils.flattenTags(readLater.tags, "id");
				$scope.readLaters.push(readLater)
			})
		}
		
		$scope.pageResult.size = result.size;
		$scope.pageResult.total = result.totalElements;
		// if there's more pages
		$scope.pageRequest.next = ((result.totalPages - (result.number + 1)) > 0) ?
				$scope.buildPageRequest(result.number + 1, result.sort) : null;
		// if there's previous pages
		$scope.pageRequest.prev = (result.number > 0) ?
				$scope.buildPageRequest(result.number - 1, result.sort) : null;
				
		// process tags
		var paramTags = $scope.getRouteParam("t");
		var selectedTags = paramTags ? paramTags.split(",") : [];
		for(var i=result.relatedTags.length-1; i >= 0; i--) {
			var tagId = result.relatedTags[i].id ? 
					result.relatedTags[i].id : result.relatedTags[i];
			if(selectedTags.indexOf(tagId) > -1) {
				result.relatedTags.splice(i, 1);
			} 
		}
		
		$scope.tags = {
				all: result.allTags,
				selected: selectedTags,
				related: result.relatedTags
		};
	}
	
	function handleRemoveSuccess(removed) {
		// clean up current view
		angular.forEach($scope.readLaters, function(readLater, index) {
			if(readLater.id === removed.id) {
				$scope.pageResult.total--;
				$scope.readLaters.splice(index, 1);
			}
		});
		
		if($scope.readLaters.length == 0) {
			if($scope.pageRequest.prev) {
				NavService.url($scope.pageRequest.prev)
			} else {
				$location.url('/urls')
			}
		}
	}
	
	/**
	 * Removes the given ReadLater. On success, the removed ReadLater
	 * is also removed from the current view.
	 */
	$scope.deleteReadLater = function(readLater) {
		// TODO: fix off by one in pagination after deleting, need to re-cal pagerequest
		TLRLService.remove({id: readLater.id}, 
			handleRemoveSuccess,
			$scope.handleError);
	}
	
	/**
	 * Extracts paging request out of location 
	 */
	$scope.currentPageRequest = function() {
		$scope.pageRequest.current = $scope.buildPageRequest(
				$scope.getRouteParam('p'), $scope.getRouteParam('s'));
		return $scope.pageRequest.current;
	}

	$scope.pageHeading = function() {
		var path = $location.path();
		if(path.indexOf('/urls/') == 0) {
			return 'Popular bookmark details';
		} else if(path.indexOf('/recent') == 0) {
			return 'Recent bookmarks';
		} else if(path.indexOf('/popular') == 0) {
			return 'Popular bookmarks';
		} else {
			var targetLink = $sce.trustAsHtml('<a href="/' + ($scope.targetUser ? '@' + ($scope.isManageable ? $scope.currentUser.name : $scope.targetUser.name) : "urls") + '">' +
				($scope.targetUser ? ($scope.isManageable ? "my" : $scope.targetUser.name + "'s") : "everyone's") + '</a>');
			if(path.indexOf('/search') != -1) 
				return 'Searching ' + targetLink + ' bookmarks';
			else 
				return 'Viewing ' + targetLink + ' bookmarks';
		}
	}
	
	$scope.$on('$routeChangeSuccess', function(e, l) {
		$scope.setTargetUser();
		$scope.handleRouteChanges();
	});

	function setLocationTagFilter(tag) {
		var _search = $location.search();
		_search.t = (tag || null);
		_search.p = null;
		$location.search(_search);
	}
	
	$scope.filterWithTag = function(tag, path) {
		var tags = ($routeParams.t || '')
		setLocationTagFilter((tags ? tags + ',' : '' ) + tag);
	}
	
	$scope.filterByTag = function(tag, path) {
		setLocationTagFilter(tag);
	}

	/**
	 * ['a','b',..] ==> 'a,b,..'
	 * Takes an array and flattens it to a string representation. Actual 
	 * process of converting array element to type that can be stringified 
	 * is handled by optional callback method. Otherwise its assumed that 
	 * current element is stringifiable as is.
	 * 
	 * @param arr to be flattened
	 * @param optional handler function to stringify current element
	 */
	//TODO: move to utils, and update current flatten method
	function flatten(arr, handler) {
		// nothing to flatten
		if(!angular.isArray(arr) || arr.length == 0)
			return arr;
		
		// check if optional handler for stringifying the element
		handler = (handler || function(e){ return e});
		return arr.reduce(function(ret, curr, i, a) {
			return ret + "," + handler(curr);
		});
	}
	
	/**
	 * 
	 */
	$scope.removeTag = function(removedTag) {
		var tags = ($routeParams.t || '').split(',');
		if(angular.isArray(tags)) {
			tags.splice(tags.indexOf(removedTag), 1)
			setLocationTagFilter(flatten(tags));
		}
	}
	
	$scope.clearTags = function() {
		$location.search('t', null);
	}
	
	
}])
.controller('popularDetailsCtrl', ['$scope', '$routeParams', '$location', 'TLRLService', '$controller', 
    function($scope, $routeParams, $location, TLRLService, $controller) {
	angular.extend(this, $controller('baseCtrl', {$scope: $scope}))
	
	console.log("inside popularDetailsCtrl")
	
	function popularDetails() {
		TLRLService.popularDetails({id: $routeParams.webUrlId}, function(resp) {
			$scope.webUrl = resp.content[0];
			console.log(resp.content.slice(1))
			$scope.bookmarks = resp.content.slice(1);
		}, $scope.handleError);
	}
	
	$scope.handleRouteChanges = function() {
		popularDetails();
	}
	
}])
.controller('popularCtrl', ['$scope', '$routeParams', '$location', 'TLRLService', '$controller', 
    function($scope, $routeParams, $location, TLRLService, $controller) {
	angular.extend(this, $controller('baseCtrl', {$scope: $scope}))
	
	$scope.findPopular = function() {
		TLRLService.popular({}, $scope.handleResultPage, $scope.handleError);
	}
	
	$scope.handleRouteChanges = function() {
		$scope.findPopular();
	}
	
}])
.controller('recentCtrl', ['$scope', '$routeParams', '$location', 'TLRLService', '$controller', 
    function($scope, $routeParams, $location, TLRLService, $controller) {
	angular.extend(this, $controller('baseCtrl', {$scope: $scope}))
	
	$scope.findRecent = function() {
		TLRLService.recent({}, $scope.handleResultPage, $scope.handleError);
	}
	
	$scope.handleRouteChanges = function() {
		$scope.findRecent();
	}
	
}])
/**
 * Controller for handling search related functionality. On initial 
 * load of this controller, search if performed if there is a query param
 */
.controller('searchCtrl', ['$scope', '$routeParams', '$location', 'TLRLService', '$controller',
    function($scope, $routeParams, $location, TLRLService, $controller) {
	
	angular.extend(this, $controller('baseCtrl', {$scope: $scope}))
	
	$scope.term = $routeParams.q;
	console.log("in search ctrl")
	$scope.searchReadLaters = function(request) {
		TLRLService.search({user: $scope.getTargetUserAtName(), q: request.term, page: 
			request.page, sort: request.sort, tags: request.tags},
			$scope.handleResultPage,
			$scope.handleError);
	}
	
	$scope.filterSearchWithTag = function(tag) {
		var tags = $scope.getRouteParam('t')
		$location.url($location.path() + '?q='+ $scope.pageRequest.current.term + 
				'&t=' +  (tags ? tags + ',' : '' ) + tag)
	}
	
	$scope.clearTags = function() {
		$location.url($location.path() + '?q=' + $scope.pageRequest.current.term)
	}
	
	$scope.handleRouteChanges = function() {
		$scope.searchReadLaters($scope.currentPageRequest());
	}
	
}])
.controller('urlsCtrl', ['$scope', '$routeParams', '$location', 
       'TLRLService', '$controller', '$filter', 'Utils', 'NavService',
    function($scope, $routeParams, $location, TLRLService, $controller, $filter, Utils, NavService) {
	
	angular.extend(this, $controller('baseCtrl', {$scope: $scope}))
	
	$scope.applyFilter = function(f) {
		$location.url($location.path() + '?f=' + f);
	}
	
	$scope.getFilters = function() {
		var search = $location.search();
		return (search.f || 'all').split(',');
	}
	
	$scope.hasFilters = function(t) {
		var filters = $scope.getFilters();
		for(var i in filters) {
			if(filters[i] == t)
				return true;
		}
		return false;
	}

	$scope.getFilterName = function() {
		var filters = $scope.getFilters();
		var name = $location.search().f;
		return (name === 'all') ? '' : name;
	}
	
	/**
	 * Performs query for ReadLaters.
	 * @param req optional request containing page and sort
	 */
	$scope.queryReadLaters = function(req) {
		TLRLService.query({user: $scope.getTargetUserAtName(), 
				page: req.page, sort: req.sort, tags: req.tags, filters: $location.search().f },
			$scope.handleResultPage,
			$scope.handleError);
		
	}
	
	$scope.handleRouteChanges = function() {
		//TODO: avoid reloading tags if tags param is unchanged?
		$scope.queryReadLaters($scope.currentPageRequest());
	}
	
}])
.directive('tlrlPage', ['$location', '$sce', 'NavService',
    function($location, $sce, NavService) {
	return {
		restrict: 'E',
		scope: {
			pageRequest: '='
		},
		link: function(scope, elem, attrs) {
			var path = $location.path();
			scope.query = function(pageRequest) {
				NavService.url(pageRequest)
			}
		},
		templateUrl: '/partials/paginate.html'
	}
}])
.directive('readLaters', ['$compile', '$http', '$templateCache', '$location', 'TLRLService',
     function($compile, $http, $templateCache, $location, TLRLService) {
	
	function getTemplate(name) {
		return $http.get('/partials/'+ name +'.html', {
			cache: $templateCache 
		})
	}
	
	return {
		restrict: 'E',
		scope: {
			readLater: '=',
			onDelete: '&',
			onSave: '&',
			targetUser: '=',
			isManageable: '='
		},
		link: function($scope, elem, attrs) {
			$scope.loadTemplate = function(mode) {
				var rawHtml = "";
				getTemplate(mode).success(function(html) {
					elem.html(html)
					elem.html($compile(elem.html())($scope))
				})
			}
			
			$scope.updateStatus = function(status) {
				TLRLService.updateStatus({id: $scope.readLater.id}, {id: $scope.readLater.id, status: status})
					.$promise.then(function(readLater) {
						$scope.readLater = readLater;
					})
			}
			
			$scope.doSaveReadLater = function(readLater) {
				// scoped in from controller ($scope.saveReadLater)
				$scope.onSave({readLater: readLater, cb: function() {
					$scope.showView();
				}})
			}
			$scope.cancel = function() {
				if(attrs.removeOnCancel) {
					// when cancelled is called right after readLater was added
					$scope.onDelete($scope.readLater)
				} else {
					// when cancel is called from a normal edit
					$scope.showView();
				}
				
			}
			$scope.showView = function() {
				if(!attrs.removeOnCancel) { // make sure we can show view
					$scope.loadTemplate('view');
				}
			}
			$scope.showEdit = function() {
				$scope.loadTemplate('edit')
			}
		},
		templateUrl: function(elem, attrs) {
			// load default view or mode if present
			return '/partials/' + (attrs.mode || 'view') +'.html';
		}
	}
	
}])


