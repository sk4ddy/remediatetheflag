/*
 *  
 * REMEDIATE THE FLAG
 * Copyright 2018 - Andrea Scaduto 
 * remediatetheflag@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
function splitValue(value, index) {
	return (value.substring(0, index) + "," + value.substring(index)).split(',');
}
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

function deepCopy (arr) {
	var out = [];
	for (var i = 0, len = arr.length; i < len; i++) {
		var item = arr[i];
		var obj = {};
		for (var k in item) {
			obj[k] = item[k];
		}
		out.push(obj);
	}
	return out;
}

Object.size = function(obj) {
	var size = 0, key;
	for (key in obj) {
		if (obj.hasOwnProperty(key)) size++;
	}
	return size;
};


jQuery.extend({
	deepclone: function(objThing) {

		if ( jQuery.isArray(objThing) ) {
			return jQuery.makeArray( jQuery.deepclone($(objThing)) );
		}
		return jQuery.extend(true, {}, objThing);
	},
});
_st = function(fRef, mDelay) {
	if(typeof fRef == "function") {
		var argu = Array.prototype.slice.call(arguments,2);
		var f = (function(){ fRef.apply(null, argu); });
		return setTimeout(f, mDelay);
	}
	try{
		return window.setTimeout(fRef, mDelay);
	}
	catch(err){

	}
}
$(document).ready(function(){
	function responsiveView() {
		var win = $(window).height();
		var n = parseInt($('.navbar').css('height').replace('px',''))
		var f = parseInt($('#footerwrap').css('height').replace('px',''))
		$('.serviceContainer').css('height',(win - n - f + 10)+"px");
	}
	responsiveView();
	$(window).on('load', responsiveView);
	$(window).on('resize', responsiveView); 
})      



var rtf = angular.module('rtfNg-signup',['nya.bootstrap.select']);

//register the interceptor as a service
var compareTo = function() {
	return {
		require: "ngModel",
		scope: {
			otherModelValue: "=compareTo"
		},
		link: function(scope, element, attributes, ngModel) {

			ngModel.$validators.compareTo = function(modelValue) {
				return modelValue == scope.otherModelValue;
			};

			scope.$watch("otherModelValue", function() {
				ngModel.$validate();
			});
		}
	};
};

rtf.directive("compareTo", compareTo);

rtf.directive('complexPassword', function() {
	return {
		require: 'ngModel',
		link: function(scope, elm, attrs, ctrl) {
			ctrl.$parsers.unshift(function(password) {
				var hasUpperCase = /[A-Z]/.test(password);
				var hasLowerCase = /[a-z]/.test(password);
				var hasNumbers = /\d/.test(password);
				var hasNonalphas = /\W/.test(password);
				var characterGroupCount = hasUpperCase + hasLowerCase + hasNumbers + hasNonalphas;

				if ((password.length >= 8) && (characterGroupCount >= 3)) {
					ctrl.$setValidity('complexity', true);
					return password;
				}
				else {
					ctrl.$setValidity('complexity', false);
					return undefined;
				}

			});
		}
	}
});

rtf.service('server',function($http,$timeout,$rootScope){ 

	this.countries = [];

	function replaceArrayContent(obj1, obj2){
		obj1.remove(0,(obj1.length-1));
		for(var i in obj2){
			obj1.push(obj2[i]);
		}
	}
	function replaceObjectContent(obj1, obj2){
		for (var key in obj1){
			if (obj1.hasOwnProperty(key)){
				delete obj1[key];
			}
		}
		for(var i in obj2){
			obj1[i] = obj2[i]
		}
	}
	
	this.checkUsernameAvailable = function(username,code){

		var msg = {};
		msg.action = 'isUsernameAvailable';
		msg.username = username;
		msg.orgCode = code;
		var req = {
				method: 'POST',
				url: '/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('usernameAvailable:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	
	this.validateInviteCode = function(code){
		
		var msg = {};
		msg.action = 'isInviteCodeValid';
		msg.orgCode = code;
		var req = {
				method: 'POST',
				url: '/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('validateInviteCode:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getCountries = function($this){

		var msg = {};
		msg.action = 'getCountries';
		var req = {
				method: 'POST',
				url: '/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			replaceArrayContent($this.countries,response.data)
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getCountries(this);

	this.doSignup = function(orgInviteCode, username,firstName,lastName,country,email,password){
		var msg = {};
		msg.action = 'doSignup';
		msg.orgCode = orgInviteCode;
		msg.username = username;
		msg.firstName = firstName;
		msg.lastName = lastName;
		msg.country = country;
		msg.email = email;
		msg.password = password;

		var req = {
				method: 'POST',
				url: '/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			if(response.data.result == "success"){
				document.location = "/postSignup.html";
			}
			else{
				alert("Signup failed, please try again or email support@remediatetheflag.com")
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

});
rtf.controller('signup',['$scope','server','$timeout',function($scope,server,$timeout){

	$scope.phase1 = true;
	$scope.phase2 = false;
	$scope.codeIsWrong = false;
	
	$scope.$on('validateInviteCode:updated', function(event,data) {
		if(data.result){
			$scope.codeIsWrong = false;
			$scope.phase1 = false;
			$scope.phase2 = true;
		}
		else{
			$scope.codeIsWrong = true;
		}
	});
	
	$scope.usernameAvailable = true;
	
	$scope.isUsernameAvailable = function(){
		if($scope.signup.username != "" )
			server.checkUsernameAvailable($scope.signup.username, $scope.signup.orgInvitationCode);
	}
	$scope.$on('usernameAvailable:updated', function(event,data) {
		$scope.usernameAvailable = data.result;
	});
	
	$scope.validateInviteCode = function(){
		server.validateInviteCode($scope.signup.orgInvitationCode);
	}

	$scope.signup = {};
	$scope.signup.orgInvitationCode = "";

	$scope.signup.username = "";	
	$scope.signup.firstName = "";
	$scope.signup.lastName = "";
	$scope.signup.selectedCountry = {};
	$scope.signup.email = "";
	$scope.signup.password = "";
	$scope.signup.repeatPassword = "";
	$scope.countries = server.countries;
	$scope.signup = function(){
		server.doSignup($scope.signup.orgInvitationCode,$scope.signup.username, $scope.signup.firstName,$scope.signup.lastName,$scope.signup.selectedCountry.short,$scope.signup.email,$scope.signup.password);
	}
}])
