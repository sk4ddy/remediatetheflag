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
Number.isInteger = Number.isInteger || function(value) {
	return typeof value === 'number' && 
	isFinite(value) && 
	Math.floor(value) === value;
};
Array.prototype.remove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};
String.prototype.trim = function () {
	return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
};
String.prototype.replaceAll = function(search, replacement) {
	var target = this;
	return target.replace(new RegExp(search, 'g'), replacement);
};
Object.size = function(obj) {
	var size = 0, key;
	for (key in obj) {
		if (obj.hasOwnProperty(key)) size++;
	}
	return size;
};
function getSum(total, num) {
	return total + num;
}
function toTitleCase(str)
{
	return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}
function htmlEncode(value){
	return $('<div/>').text(value).html();
}    
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
function cloneObj(obj){
	return JSON.parse(JSON.stringify(obj))
}
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
jQuery.extend({
	deepclone: function(objThing) {

		if ( jQuery.isArray(objThing) ) {
			return jQuery.makeArray( jQuery.deepclone($(objThing)) );
		}
		return jQuery.extend(true, {}, objThing);
	},
});
function splitValue(value, index) {
	return (value.substring(0, index) + "," + value.substring(index)).split(',');
}
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
PNotify.prototype.options.stack.firstpos1 = 80; // or whatever pixel value you want.

function responsiveView() {
	var win = $(window).height();

	var n = parseInt($('.navbar').css('height').replace('px',''))

	var s1 = parseInt($('.serviceContainer').css('padding-top').replace('px',''))
	var s2 = parseInt($('.serviceContainer').css('padding-bottom').replace('px',''))
	var s3 = parseInt($('.serviceContainer').css('margin-top').replace('px',''))
	var s4 = parseInt($('.serviceContainer').css('margin-bottom').replace('px',''))
	var sH = s1 + s2 + s3 + s4;

	var sa1 = parseInt($('.standalone').css('height').replace('px','')) 

	var f = parseInt($('#footerwrap').css('height').replace('px',''))
	var smb = parseInt($('.standalone').css('margin-bottom').replace('px',''))
	var h = parseInt($('#headerwrap').css('height').replace('px',''))

	var calc1 = (n + f + smb);
	var calc2 = (n + h + f +sH);

	$('.standalone').css('min-height',(win - calc1 + 10)+"px");
	$('.serviceContainer').css('height',(win - calc2 + 10)+"px");
}

$(document).ready(function(){
	responsiveView();
	$(window).on('load', responsiveView);
	$(window).on('resize', responsiveView); 

	$('#targetDiv').on('click','.snippetScroll', function(e){
		e.preventDefault();
		var offset = $('#'+$(this).attr('path')).offset();
		$('html, body').animate({
			scrollTop: offset.top - 50,
			scrollLeft: offset.left
		});
	})
	$('#completedTargetDiv').on('click','.snippetScroll', function(e){
		e.preventDefault();
		var offset = $('#'+$(this).attr('path')).offset();
		$('html, body').animate({
			scrollTop: offset.top - 50,
			scrollLeft: offset.left
		});
	})
});
var rtf = angular.module('rtfNg',['nya.bootstrap.select','jlareau.pnotify','ngRoute','ui.toggle','angular-table','chart.js','angular-notification-icons','angularSpinner']);


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
rtf.filter('capitalize', function() {
	return function(input) {
		return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
	}
});
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
rtf.service('server',function($http,$timeout,$rootScope,notificationService,$interval){ 

	this.countries = [];
	this.addOrganization = function(obj){

		obj.action = 'addOrganization';

		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: obj,
		}
		$http(req).then(function successCallback(response) {
			if(response.data.result=="success")
				notificationService.success('Organization added.');
			else
				notificationService.notice('Updated failed, please try again.');
			$rootScope.$broadcast('organizationAdded:updated',response.data);

		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.getOrganizations = function($this){

		var msg = {};
		msg.action = 'getOrganizations';

		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('organizations:updated',response.data);

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
	this.user = {}

	this.getUserProfileInfo = function(){
		var msg = {};
		msg.action = 'getUserInfo';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('userProfile:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.getChallengeDetails = function(id){
		var msg = {};
		msg.action = 'getChallengeDetails';
		msg.id = id;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg
		}
		$http(req).then(function successCallback(response) {
			if(response.data.length==0)
				$rootScope.$broadcast('challengeDetails:updated',null);
			else
				$rootScope.$broadcast('challengeDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.getChallenges = function(){

		var msg = {};
		msg.action = 'getChallenges';

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg
		}
		$http(req).then(function successCallback(response) {
			if(response.data.length==0)
				$rootScope.$broadcast('challenges:updated',null);
			else
				$rootScope.$broadcast('challenges:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}    

	this.getUserProfile = function($this){

		var msg = {};
		msg.action = 'getUserInfo';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			replaceObjectContent($this.user,response.data);
			$rootScope.$broadcast('userProfile:updated',response.data);
			$('.waitLoader').hide();
			$this.getTeams();
			$this.getUnreadNotifications();
			if(response.data.r!= 3){
				$this.getUsers();
				$this.getGlobalStats([]);
			}
			if(response.data.r <= 3){
				$this.getPendingReviews();
				$this.getCompletedReviews();
				$this.getAvailableExercises();
				$this.getChallenges();
			}
			if(response.data.r == 0){
				$this.getOrganizations();
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.updateUserProfile = function(updatedUser){
		var msg = {};
		msg.action = 'setUserInfo';
		msg.firstName = updatedUser.firstName;
		msg.lastName = updatedUser.lastName;
		msg.email = updatedUser.email;
		msg.country = updatedUser.country.short;

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success")
				notificationService.success('User profile updated.');
			else
				notificationService.notice('Updated failed, please try again.');
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.addUser = function(user){
		var msg = {};
		msg.action = 'addUser';
		msg.username = user.username;
		msg.firstName = user.firstName;
		msg.lastName = user.lastName;
		msg.email = user.email;
		msg.country = user.country.short;
		msg.roleId = user.role.id;
		msg.concurrentExercisesLimit = user.concurrentExercisesLimit;
		msg.password = user.password;
		msg.emailVerified = user.emailVerified;
		msg.forcePasswordChange = user.forcePasswordChange;
		msg.orgId = user.organization.id;

		var req = {
				method: 'POST',
				url: '/management/reviewer/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="error"){
				notificationService.notice('Updated failed, please try again.');
			}
			else{
				notificationService.success('User added.');
				$rootScope.$broadcast('userAdded:updated',response.data);
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}


	this.addUsersToTeam = function(userList, teamName){
		var msg = {};
		msg.action = 'addUsersToTeam';
		msg.users = userList;
		msg.teamName = teamName;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="error"){
				notificationService.notice('Updated failed, please try again.');
			}
			else{
				notificationService.success('Users added to team.');
				$rootScope.$broadcast('usersAddedToTeam:updated',response.data);
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getRegionsForExercise = function(id){

		var msg = {};
		msg.action = 'getRegionsForExercise';
		msg.id = id;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('exerciseRegions:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getExerciseDetails = function(id){

		var msg = {};
		msg.action = 'getExerciseDetails';
		msg.id = id;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('exerciseDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.isTeamNameAvailable = function(name){

		var msg = {};
		msg.action = 'checkTeamNameAvailable';
		msg.name = name;
		var req = {
				method: 'POST',
				url: '/management/reviewer/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('teamNameAvailable:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.isOrgNameAvailable = function(name){

		var msg = {};
		msg.action = 'checkOrganizationNameAvailable';
		msg.name = name;
		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('orgNameAvailable:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.checkUsernameAvailable = function(username){

		var msg = {};
		msg.action = 'isUsernameAvailable';
		msg.username = username;
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

	this.sendNotification = function(username,text){
		var msg = {};
		msg.action = 'sendNotification';
		msg.text = text;
		msg.username = username;

		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('Notification sent.');
				$rootScope.$broadcast('notificationSent:updated',response.data);
			}
			else if(response.data.result=="error"){
				notificationService.notice('Action failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.removeTeamManager = function(teamId,username){
		var msg = {};
		msg.action = 'removeTeamManager';
		msg.teamId = teamId;
		msg.username = username;

		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('User removed from team managers.');
				$rootScope.$broadcast('makeTeamManager:updated',response.data);
			}
			else if(response.data.result=="error"){
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.makeTeamManager = function(teamId,username){
		var msg = {};
		msg.action = 'addTeamManager';
		msg.teamId = teamId;
		msg.username = username;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('User added as team manager.');
				$rootScope.$broadcast('makeTeamManager:updated',response.data);
			}
			else if(response.data.result=="error"){
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.removeFromTeam = function(teamId,username){
		var msg = {};
		msg.action = 'removeFromTeam';
		msg.teamId = teamId;
		msg.username = username;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('User removed from team.');
				$rootScope.$broadcast('deletedFromTeam:updated',response.data);
			}
			else if(response.data.result=="error"){
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.deleteTeam = function(teamId){
		var msg = {};
		msg.action = 'deleteTeam';
		msg.teamId = teamId;

		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('Team deleted.');
				$rootScope.$broadcast('deletedTeam:updated',response.data);
			}
			else if(response.data.result=="error"){
				if(response.data.errorMsg="TeamNotEmpty")
					notificationService.notice('Team cannot be deleted, please remove all members first.');
				else
					notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.addTeam = function(teamName,orgName){
		var msg = {};
		msg.action = 'addTeam';
		msg.teamName = teamName;
		msg.orgName = orgName;

		var req = {
				method: 'POST',
				url: '/management/reviewer/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('Team created.');
				$rootScope.$broadcast('addTeam:updated',response.data);
			}
			else if(response.data.result=="error"){
				if(response.data.errorMsg=="TeamExists"){
					notificationService.notice('Specified team name already exists.');
				}
				else{
					notificationService.notice('Updated failed, please try again.');
				}
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getAvailableExercises = function(){

		var msg = {};
		msg.action = 'getExercises';

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('availableExercises:updated',response.data);

		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.enableExerciseForOrg = function(idExercise,idOrg){
		var msg = {};
		msg.action = 'enableExerciseForOrg';
		msg.orgId = idOrg;
		msg.exercise = idExercise;
		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			if(undefined==response.data.error){
				$rootScope.$broadcast('enableExerciseForOrg:updated',response.data);
			}
			else{
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.disableExerciseForOrg = function(idExercise,idOrg){
		var msg = {};
		msg.orgId = idOrg;
		msg.exercise = idExercise;
		msg.action = 'disableExerciseForOrg';

		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			if(undefined==response.data.error){
				$rootScope.$broadcast('disableExerciseForOrg:updated',response.data);
			}
			else{
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getUnreadNotifications = function(){

		var msg = {};
		msg.action = 'getNotifications';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('unreadNotifications:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.markNotificationAsRead = function(idNotification){

		var msg = {};
		msg.action = 'markNotificationRead';
		msg.id = idNotification;
		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			if(undefined==response.data.error){
				$rootScope.$broadcast('unreadNotifications:updated',response.data);
			}
			else{
				notificationService.notice('Updated failed, please try again.');
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.updateUserPassword = function(oldPassword, newPassword){
		var msg = {};
		msg.action = 'setUserPassword';
		msg.oldPwd = oldPassword;
		msg.newPwd = newPassword;
		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success")
				notificationService.success('User password updated.');
			else
				notificationService.notice('Updated failed, please try again.');

		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.resetUserPassword = function(usr, pwd){
		var msg = {};
		msg.action = 'resetUserPassword';
		msg.username = usr;
		msg.password = pwd;
		var req = {
				method: 'POST',
				url: '/management/admin/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success")
				notificationService.success('User password resetted.');
			else
				notificationService.notice('Updated failed, please try again.');
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.unlockUserAccount = function(username){
		var msg = {};
		msg.action = 'unlockUserAccount';
		msg.username = username;
		var req = {
				method: 'POST',
				url: '/management/reviewer/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			if(response.data.result=="success"){
				notificationService.success('User account unlocked.');
				$rootScope.$broadcast('accountUnlocked:updated',response.data);
			}
			else
				notificationService.notice('Updated failed, please try again.');
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.doUserLogout = function(){
		var msg = {};
		msg.action = 'doLogout';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$(document).attr('location', "/");
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	$rootScope.ctoken = "";

	this.getCToken = function($this){
		var msg = {};
		msg.action = 'getUserCToken';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.ctoken = response.data.ctoken;
			$this.getInitialData();
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getUsers = function(){
		var msg = {};
		msg.action = 'getUsers';
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('usersList:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getGlobalStats = function(filter){
		var msg = {};
		msg.action = 'getGlobalStats';
		msg.filter = filter;
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$('#waitLoader').show();
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('stats:updated',response.data);
			$('#waitLoader').hide();
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getUserStats = function(username){
		var msg = {};
		msg.username = username;
		msg.action = 'getUserStats';
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('statsUser:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getUserExercises = function(username){
		var msg = {};
		msg.username = username;
		msg.action = 'getUserExercises';
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('userCompletedExercises:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}


	this.getTeamStats = function(teamName){
		var msg = {};
		msg.name = teamName;
		msg.action = 'getTeamStats';
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('statsTeam:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getUserAchievements = function(usr){
		var msg = {};
		msg.action = 'getUserAchievements';
		msg.username = usr;
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('userAchievements:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.renameTeam = function(teamId,teamName){
		var msg = {};
		msg.action = 'renameTeam';
		msg.teamId = teamId;
		msg.name = teamName;
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('teamRenamed:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getUserDetails = function(usr){
		var msg = {};
		msg.action = 'getUserDetails';
		msg.username = usr;
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('userDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getCompletedReviews = function(){
		var msg = {};
		msg.action = 'getCompletedReviews';
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('completedReviews:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getTeams = function(){
		var msg = {};
		msg.action = 'getTeams';
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('teamsList:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getTeamDetails = function(teamName){
		var msg = {};
		msg.action = 'getTeamDetails';
		msg.name = teamName;
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('teamDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}
	this.getTeamMembers = function(teamName){
		var msg = {};
		msg.action = 'getTeamMembers';
		msg.name = decodeURIComponent(teamName);
		var req = {
				method: 'POST',
				url: '/management/stats/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('teamMembers:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getCToken(this);

	this.getInitialData = function(){  
		this.getCountries(this);
		this.getUserProfile(this);
		initialData = true;
	}

	this.getPendingReviews = function(){
		var msg = {};
		msg.action = 'getPendingReviews';

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('pendingReviews:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.getPendingReviewDetails = function(id){
		var msg = {};
		msg.action = 'getReviewDetails';
		msg.id = id
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('pendingReviewDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.getCompletedReviewDetails = function(id){
		var msg = {};
		msg.action = 'getReviewDetails';
		msg.id = id
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('completedReviewDetails:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}
	this.getUserFeedback = function(id){
		var msg = {};
		msg.action = 'getUserFeedback';
		msg.id = id
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.$broadcast('userFeedback:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.submitReview = function(o){
		var msg = {}
		msg.action = 'postReview';
		msg.obj = o;
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			notificationService.success('Review Submitted');
			$rootScope.$broadcast('reviewSubmitted:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}

	this.getAvailableUsersForTeam = function(teamName){
		var msg ={};
		msg.teamName = teamName;
		msg.action = 'getAvailableUsersForTeam';
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			$rootScope.$broadcast('availableUsersTeam:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});


	}

	this.downloadExerciseSolutions = function(id){

		var msg = {};
		msg.action = 'getSolutionFile';
		msg.id = id;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
				responseType: "blob"
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			var filename = "";                   
			var disposition = response.headers("Content-Disposition")
			var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
			var matches = filenameRegex.exec(disposition);
			if (matches !== null && matches[1]) 
				filename = matches[1].replace(/['"]/g, '');

			var blob = new Blob([response.data], { type:"application/pdf;" });		
			if (navigator.appVersion.toString().indexOf('.NET') > 0){
				window.navigator.msSaveBlob(blob, filename);
			}
			else{
				var downloadLink = angular.element('<a></a>');
				downloadLink.attr('href',window.URL.createObjectURL(blob));
				downloadLink.attr('download', filename);
				downloadLink[0].click();
			}

		}, function errorCallback(response) {
			console.log('ajax error');
		});

	}

	this.downloadExerciseReference = function(id){

		var msg = {};
		msg.action = 'getReferenceFile';
		msg.id = id;

		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
				responseType: "blob"
		}
		$('.waitLoader').show();
		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();
			var filename = "";                   
			var disposition = response.headers("Content-Disposition")
			var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
			var matches = filenameRegex.exec(disposition);
			if (matches !== null && matches[1]) 
				filename = matches[1].replace(/['"]/g, '');

			var blob = new Blob([response.data], { type:"application/pdf;" });	
			if (navigator.appVersion.toString().indexOf('.NET') > 0){
				window.navigator.msSaveBlob(blob, filename);
			}
			else{
				var downloadLink = angular.element('<a></a>');
				downloadLink.attr('href',window.URL.createObjectURL(blob));
				downloadLink.attr('download', filename);
				downloadLink[0].click();
			}
		}, function errorCallback(response) {
			console.log('ajax error');
		});
	}



	this.markCancelled = function(id){
		var msg ={};
		msg.action = 'markAsCancelled';
		msg.id = id;
		var req = {
				method: 'POST',
				url: '/management/team/handler',
				data: msg,
		}
		$('.waitLoader').show();

		$http(req).then(function successCallback(response) {
			$('.waitLoader').hide();

			notificationService.success('Exercise marked as cancelled.');
			$rootScope.$broadcast('reviewCancelled:updated',response.data);
		}, function errorCallback(response) {
			console.log('ajax error');
		});


	}



})
rtf.controller('navigation',['$rootScope','$scope','server','$timeout','$http','$location',function($rootScope,$scope,server,$timeout,$http,$location){
	$scope.user = server.user;


	updateData = function(){
		if($rootScope.ctoken == ""){
			return;
		}
		console.log('updating data...')

		server.getTeams();
		server.getUnreadNotifications();
		if(server.user.r != 3){
			server.getUsers();
			server.getGlobalStats([]);
		}
		if(server.user.r <= 3){
			server.getPendingReviews();
			server.getCompletedReviews();
			server.getAvailableExercises();
			server.getChallenges();
		}
		if(server.user.r == 0){
			server.getOrganizations();
		}
	}
	setInterval(function(){updateData()},100000)

	$scope.logout = function(){
		server.doUserLogout();
	}
	$scope.notifications = [];
	var ctokenReq = {
			method: 'POST',
			url: '/user/handler',
			data: { action: 'getUserCToken'}
	}
	$scope.goTo = function(link){
		$location.path(link.replace("#",""), false);
	}
	$scope.$on('unreadNotifications:updated', function(event,data) {
		$scope.notifications = data; 
	})
	$scope.markNotificationRead = function(id){
		server.markNotificationAsRead(id);
	}

	$rootScope.$on('$locationChangeSuccess', function(event, newUrl, oldUrl){
		if(newUrl.indexOf('d2h-')>-1){
			return;
		}
		var target = newUrl.substr(newUrl.indexOf("#")).replace("#","").replace("/","");
		target = target.split("/")
		switch(target[0]){       
		case "home":
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.welcome = true;
			$rootScope.visibility.home = true;
			$rootScope.visibility.users = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.challenges = false;
			_st(responsiveView,200);
			$(window).scrollTop(0);
			break;
		case "challenges":
			if(target[1]=="details" && undefined!=target[2] && ""!=target[2]){
				var exId = target[2]
				if($rootScope.ctoken == ""){
					$http(ctokenReq).then(function successCallback(response) {
						$rootScope.ctoken = response.data.ctoken;
						server.getChallengeDetails(exId);
					}, function errorCallback(response) {
						console.log('ajax error');
					});
				}
				else{
					server.getChallengeDetails(exId);
				}
			}
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.challenges = true;
			$(window).scrollTop(0);
			break;
		case "users":
			if(target[1]=="details" && undefined!=target[2] && ""!=target[2]){
				try{
					username = decodeURIComponent(target[2])
				}catch(err){
					username = target[2];
				}
				if($rootScope.ctoken == ""){
					$http(ctokenReq).then(function successCallback(response) {
						$rootScope.ctoken = response.data.ctoken;
						server.getUserDetails(username);
						server.getUserStats(username);
						server.getUserExercises(username);
						server.getUserAchievements(username);
						$rootScope.showUserList = false;
						$rootScope.showUserDetails = true;
						return;
					}, function errorCallback(response) {
						console.log('ajax error');
					});
				}
				server.getUserDetails(username);
				server.getUserStats(username);
				server.getUserExercises(username);
				server.getUserAchievements(username);
				$rootScope.showUserList = false;
				$rootScope.showUserDetails = true;
			}
			else{
				$rootScope.showUserList = true;
				$rootScope.showUserDetails = false;
			}
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.users = true;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.stats = false;
			$(window).scrollTop(0);
			break;
		case "review":
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.review = true;
			$(window).scrollTop(0);
			break;
		case "teams":
			if(target[1]=="details" && undefined!=target[2] && ""!=target[2]){
				try{
					name = decodeURIComponent(target[2])
				}catch(err){
					name = target[2];
				}
				if($rootScope.ctoken == ""){
					$http(ctokenReq).then(function successCallback(response) {
						$rootScope.ctoken = response.data.ctoken;
						$rootScope.selectedTeam = name;
						server.getTeamMembers(name);
						server.getTeamStats(name);
						server.getTeamDetails(name);
						$rootScope.showTeamList = false;
						$rootScope.showTeamMembers = true;
						return;
					}, function errorCallback(response) {
						console.log('ajax error');
					});
				}
				else{
					$rootScope.selectedTeam = name;
					server.getTeamMembers(name);
					server.getTeamStats(name);
					server.getTeamDetails(name);
					$rootScope.showTeamList = false;
					$rootScope.showTeamMembers = true;
				}
				$rootScope.showTeamList = false;
				$rootScope.showTeamMembers = true;
			}
			else{
				$rootScope.showTeamList = true;
				$rootScope.showTeamMembers = false;
			}
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.teams = true;
			$(window).scrollTop(0);
			break;
		case "exercises":
			if(target[1]=="details" && undefined!=target[2] && ""!=target[2]){
				var eId = target[2];
				if($rootScope.ctoken == ""){
					$http(ctokenReq).then(function successCallback(response) {
						server.getCompletedReviewDetails(eId);
						server.getUserFeedback(eId);
					}, function errorCallback(response) {
						console.log('ajax error');
					});
				}
				else{
					server.getCompletedReviewDetails(eId);
					server.getUserFeedback(eId);
				}
			}
			else{
				$(window).scrollTop(0);
			}
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.exercises = true;
			break;
		case "stats":
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.stats = true;
			$(window).scrollTop(0);
			break;
		case "settings":
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.users = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.settings = true;
			$(window).scrollTop(0);
			break;
		case "available-exercises":
			if(target[3]=="flags" || target[3] == "info"){
				return;
			}
			if(target[1]=="details"){
				$rootScope.visibility.users = false;
				$rootScope.visibility.welcome = false;
				$rootScope.visibility.stats = false;
				$rootScope.visibility.home = false;
				$rootScope.visibility.review = false;
				$rootScope.visibility.organizations = false;
				$rootScope.visibility.exercises = false;
				$rootScope.visibility.settings = false;
				$rootScope.visibility.challenges = false;
				$rootScope.visibility.teams = false;
				$rootScope.visibility.availableExercises = true;
				if(Number.isInteger(parseInt(target[2])) && undefined==$rootScope.exerciseDetails.id){
					getExDetails(parseInt(target[2]));
				}
			}
			else{
				$rootScope.visibility.users = false;
				$rootScope.visibility.welcome = false;
				$rootScope.visibility.stats = false;
				$rootScope.visibility.home = false;
				$rootScope.visibility.review = false;
				$rootScope.visibility.organizations = false;
				$rootScope.visibility.exercises = false;
				$rootScope.visibility.settings = false;
				$rootScope.visibility.teams = false;
				$rootScope.visibility.challenges = false;
				$rootScope.visibility.availableExercises = true;
				$rootScope.showExerciseList = true;
				$rootScope.showExerciseDetails = false;
			}
			$(window).scrollTop(0);
			break;
		case "organizations":
			$rootScope.visibility.users = false;
			$rootScope.visibility.welcome = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.home = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.organizations = true;
			$(window).scrollTop(0);
			break;
		default:{
			$rootScope.visibility.availableExercises = false;
			$rootScope.visibility.welcome = true;
			$rootScope.visibility.users = false;
			$rootScope.visibility.stats = false;
			$rootScope.visibility.organizations = false;
			$rootScope.visibility.review = false;
			$rootScope.visibility.settings = false;
			$rootScope.visibility.exercises = false;
			$rootScope.visibility.teams = false;
			$rootScope.visibility.challenges = false;
			$rootScope.visibility.home = true;
			$(window).scrollTop(0);
			break;
		}

		}        

	});

	function getExDetails(id){

		var msg = {};
		msg.action = 'getUserCToken';

		var req = {
				method: 'POST',
				url: '/user/handler',
				data: msg,
		}
		$http(req).then(function successCallback(response) {
			$rootScope.ctoken = response.data.ctoken;
			server.getExerciseDetails(id);
			server.getRegionsForExercise(id);
			//	server.getInitialData();
		}, function errorCallback(response) {
			console.log('ajax error');
		});


	}

	$rootScope.visibility = {}
	$rootScope.visibility.availableExercises = false;
	$rootScope.visibility.welcome = true;
	$rootScope.visibility.users = false;
	$rootScope.visibility.exercises = false;
	$rootScope.visibility.home = true;
	$rootScope.visibility.organizations = false;
	$rootScope.visibility.review = false;
	$rootScope.visibility.settings = false;
	$rootScope.visibility.teams = false;
	$rootScope.visibility.stats = false;

}])



rtf.factory('xhrInterceptor', ['$q','$rootScope', function($q, $rootScope) {
	return {
		'request': function(config) {
			if((config.url == "/user/handler" || config.url == "/management/reviewer/handler" ||config.url == "/management/stats/handler" ||config.url == "/management/admin/handler" ||config.url == "/management/team/handler") && config.data.action !=undefined && config.data.action != "getUserCToken")
				config.data.ctoken = $rootScope.ctoken;
			return config;
		},
		'response': function(response) {
			if(undefined!=response && undefined!=response.data){
				try{
					if(response.data.indexOf('loginButton')>0){
						document.location = "/index.html";
					}
				}catch(err){}
			}
			if(undefined!=response && undefined!=response.data && undefined!=response.data.errorMsg && response.data.errorMsg=="ChangePassword"){
				document.location = "/user/changePassword.html";
				return;
			}
			return response;
		}
	};
}]);
rtf.run(['$route', '$rootScope', '$location', function ($route, $rootScope, $location) {
	var original = $location.path;
	$location.path = function (path, reload) {
		if (reload === false) {
			var lastRoute = $route.current;
			var un = $rootScope.$on('$locationChangeSuccess', function () {
				$route.current = lastRoute;
				un();
			});
		}
		return original.apply($location, [path]);
	};
}])


rtf.config(['$httpProvider', function($httpProvider) {  
	$httpProvider.interceptors.push('xhrInterceptor');
}]);

rtf.controller('welcome',['$scope','server',function($scope,server){
	$scope.user = server.user;
}])
rtf.controller('home',['$scope','server',function($scope,server){
	$scope.user = server.user;
}])
rtf.controller('organizations',['$scope','server','$filter',function($scope,server,$filter){

	$scope.masterOrganizations = [];
	$scope.filteredOrganizationsList = []; 
	$scope.selectedOrganizationRow = -1;
	$scope.organizationstableconfig = {
			itemsPerPage: 10,
			fillLastPage: false
	}
	$scope.getReviewedDate = function(date){
		if(null!=date && undefined != date)
			return moment(date).format("MMM D, YYYY")
			else
				return "N/A";
	}
	$scope.updateFilteredList = function() {
		$scope.filteredOrganizationsList = $filter("filter")($scope.masterOrganizations, $scope.query);
	};

	$scope.$on('organizations:updated', function(event,data) {
		if(data.result=="success"){
			server.getOrganizations();
		}
	});
	$scope.$on('organizations:updated', function(event,data) {
		$scope.masterOrganizations = data;
		$scope.filteredOrganizationsList = $scope.masterOrganizations;
	});
	$scope.isOrgNameAvailable = function(){
		if($scope.newOrg.name != "" )
			server.isOrgNameAvailable($scope.newOrg.name);
	}
	$scope.$on('orgNameAvailable:updated', function(event,data) {
		$scope.orgNameAvailable = data.result;
	});
	$scope.orgNameAvailable = true;
	$scope.newOrg = {};
	$scope.newOrg.name = "";
	$scope.newOrg.contactName = "";
	$scope.newOrg.contactTelephone = "";
	$scope.newOrg.contactEmail = "";
	$scope.newOrg.maxUsers = 100;
	$scope.newOrg.allowedDomains = "";

	$scope.addOrganizationModal= function(){
		$('#addOrgModal').modal('show');
	}

	$scope.addOrganization = function(){
		var obj = {};
		var arr = $scope.newOrg.allowedDomains.replaceAll(" ","").replaceAll("\n","").replaceAll("\t","").split(",");
		if(arr[0]==undefined)
			obj.allowedDomains = "*";
		else
			obj.allowedDomains = arr;
		obj.name = $scope.newOrg.name;
		obj.contactName = $scope.newOrg.contactName;
		obj.contactPhone = $scope.newOrg.contactTelephone;
		obj.contactEmail = $scope.newOrg.contactEmail;
		obj.maxUsers = $scope.newOrg.maxUsers;
		server.addOrganization(obj);
		$('#addOrgModal').modal('hide');

		$scope.newOrg.name = "";
		$scope.newOrg.contactTelephone = "";
		$scope.newOrg.contactName = "";
		$scope.newOrg.contactEmail = "";
		$scope.newOrg.allowedDomains = "";
		$scope.newOrg.maxUsers = 100;
	}


	$scope.$on('organizationAdded:updated', function(event,data) {
		if(data.result=="success"){
			server.getOrganizations();
			server.getUserProfileInfo();
		}
	});
}])
rtf.controller('completedExercises',['$scope','server','$rootScope','$filter',function($scope,server,$rootScope,$filter){
	$scope.masterCompletedReviews = [];
	$scope.hideCancelled = true;
	$scope.filteredCompletedList = []; 
	$scope.selectedCompletedRow = -1;
	$scope.showCompletedResult = false;
	$scope.showCompletedCodeDiff = false;
	$scope.showCompletedLogs = false;
	$scope.query = "";
	$scope.userFeedback = "";
	$scope.completedZipError = false;
	$scope.completedEmptyLog = false;
	$scope.completedEmptyDiff = false;
	$scope.completedtableconfig = {
			itemsPerPage: 10,
			fillLastPage: false
	}
	$scope.$on('userFeedback:updated', function(event,data) {
		$scope.userFeedback = data.id;
	});


	$scope.$watch("hideCancelled", function() {
		$scope.updateFilteredList();
	});

	$scope.getReviewedDate = function(date){
		if(null!=date && undefined != date)
			return moment(date).format("MMM D, HH:mm")
			else
				return "N/A";
	}

	$scope.updateFilteredList = function() {
		$scope.filteredCompletedList = $filter("filter")($scope.masterCompletedReviews, $scope.query);
		if($scope.hideCancelled){
			$scope.filteredCompletedList = $filter("filter")($scope.filteredCompletedList,{ status:"!CANCELLED"});
		}
	};

	$scope.$on('completedReviews:updated', function(event,data) {
		$scope.masterCompletedReviews = data;
		$scope.filteredCompletedList = $filter("filter")($scope.masterCompletedReviews, "!CANCELLED");
	});
	$scope.getDurationInterval = function(start,end){
		var out = moment.utc(moment(end).diff(moment(start))).format("H mm").replace(" ","h")+"'";
		return out;
	};
	$scope.getMinutesDuration = function(dur){
		if(undefined != dur && null != dur && 0 != dur){
			return moment.utc(moment.duration(dur,"m").asMilliseconds()).format("H mm").replace(" ","h")+"'";
		}
		else{
			return "N/A"
		}
	};
	$scope.getDatesInterval = function(start,end){
		var out = moment(start).format("MMM D YYYY, HH:mm");
		out += " - "+moment(end).format("HH:mm");
		return out;
	}
	var statusClassMap = {
			"REVIEWED": "table-success",
			"CANCELLED": "table-warning"
	}
	$scope.getStatusClass = function(status) {
		return statusClassMap[status]
	};
	$scope.getStatusString = function(status){
		switch(status){
		case "1":
			return "Vulnerable"
		case "0":
			return "Not Vulnerable"
		case "2":
			return "Broken Functionality"
		case "4":
			return "Not Addressed"

		default: return "";
		}
	}
	var remediationClassMap = {
			"0": "table-success",
			"1": "table-danger",
			"2": "table-warning",
			"4": "table-info"
	};

	$scope.getRemediationClass = function(status){
		return remediationClassMap[status]
	};

	$scope.toggleCompletedCodeDiff = function(){
		if($scope.showCompletedCodeDiff == true)
			$scope.showCompletedCodeDiff = false;
		else{
			$scope.showCompletedLogs = false;
			$scope.showCompletedCodeDiff = true;
		}
	}
	$scope.toggleCompletedInstanceLogs = function(){
		if($scope.showCompletedLogs == true)
			$scope.showCompletedLogs = false;
		else{
			$scope.showCompletedCodeDiff = false;
			$scope.showCompletedLogs = true;
		}
	}
	$scope.$on('completedReviewDetails:updated', function(event,data) {



		for(var i in data.results){
			switch(data.results[i].status){
			case "VULNERABLE":
				data.results[i].status = "Vulnerable"
					break;
			case "NOT_VULNERABLE":
				data.results[i].status = "Not Vulnerable"
					break;
			case "BROKEN_FUNCTIONALITY":
				data.results[i].status = "Broken Functionality"
					break;
			case "NOT_AVAILABLE":
				data.results[i].status = "N/A"
					break;
			case "NOT_ADDRESSED":
				data.results[i].status = "Not Addressed"
					break;
			}


		}
		if(data.results.length == 0){
			data.results = [];
			for(var j in data.exercise.flags){
				data.results[j] = {};
				data.results[j].name = data.exercise.flags[j].title;
				data.results[j].status = "N/A";
				data.results[j].verified = false; 
			}
		}

		$scope.completedItemDetails = data;
		$scope.showCompletedResult = false;
		$scope.showCompletedCodeDiff = false;

		$scope.showCompletedResult = true;
		$scope.completedZipError = false;
		$scope.completedEmptyLog = false;
		$scope.completedEmptyDiff = false;



		JSZipUtils.getBinaryContent($scope.completedItemDetails.id,$rootScope.ctoken,'/management/team/handler','getReviewFile', function(err, data) {
			if(err) {
				throw err; // or handle err
				$scope.completedZipError = true;
			}

			JSZip.loadAsync(data).then(function(zip) {
				for(file in zip.files){
					if(file.indexOf('sourceDiff.txt')>-1){
						zip.file(file).async("string").then(function success(content) {
							var diffString = content;
							if(diffString==""){
								$scope.completedEmptyDiff = true;
								$('#completedTargetDiv').empty()
								return;
							}
							try{
								var diff2htmlUi = new Diff2HtmlUI({diff : diffString });
								diff2htmlUi.draw( '#completedTargetDiv', {
									inputFormat : 'diff',
									showFiles : true,
									matching : 'lines'
								});
								diff2htmlUi.highlightCode('#completedTargetDiv');
							} catch(err){
							}
						})                        
					}
					else if(file.indexOf('rtf.log')>-1){
						zip.file(file).async("string").then(function success(content) {
							var resultString = content;
							if(resultString==""){
								$scope.completedEmptyLog = true;
								$('#completedRtfLogText').empty()
								return;
							}
							rtfLog = resultString;
							var datas = rtfLog.split("\n");
							var tba = "";
							for (var i = 0; i < datas.length; i++) {
								if (datas[i] !== "") {
									tba += '<li class="list-group-item">' + htmlEncode(datas[i]) + '</li>';
								}
							}
							$('#completedRtfLogText').append(tba);
							var diff2htmlUi = new Diff2HtmlUI();
							diff2htmlUi.highlightCode('#completedRtfLogText');
							diff2htmlUi.highlightCode('.list-group-item');
						});   
					}
				}
			});
		});    
		setTimeout(function(){
			var offset = $('#showCompletedResults').offset();
			$('html, body').animate({
				scrollTop: offset.top - 50,
				scrollLeft: offset.left
			});
		},800);

	})

	$scope.showCompletedDetailsFor = function(eId, feedback, index){
		server.getCompletedReviewDetails(eId);
		if(feedback){
			server.getUserFeedback(eId);
		}
		else{
			$scope.userFeedback = "";
		}
		$scope.selectedCompletedRow = index;
	}
}])
rtf.controller('users',['$scope','server','$location','$rootScope','$filter',function($scope,server,$location,$rootScope,$filter){
	$scope.masterUsersList = [];
	$scope.filteredUsersList = []; 

	$scope.remediatedPerIssue = {}
	$scope.remediatedPerIssue.data = [];
	$scope.remediatedPerIssue.labels = [];
	$scope.remediatedPerCategory = {}
	$scope.remediatedPerCategory.data = [];
	$scope.remediatedPerCategory.labels = [];
	$scope.remediationRatePerIssue = [];
	$scope.remediationRatePerCategory = [];
	$scope.scorePerExercises = {};
	$scope.options = {}
	$scope.options.layout = {
			padding: {
				left: 0,
				right: 0,
				top: 0,
				bottom: 0
			}
	}
	$scope.options.animation = false;
	$scope.options.pieceLabel = {
			// render 'label', 'value', 'percentage' or custom function, default is 'percentage'
			render: 'percentage',
			// precision for percentage, default is 0
			precision: 0,
			// identifies whether or not labels of value 0 are displayed, default is false
			showZero: false,
			// font size, default is defaultFontSize
			fontSize: 12,
			// font color, can be color array for each data, default is defaultFontColor
			fontColor: '#FFF',
			// font style, default is defaultFontStyle
			fontStyle: 'normal',
			// font family, default is defaultFontFamily
			fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
			// draw label in arc, default is false
			arc: false,
			// position to draw label, available value is 'default', 'border' and 'outside'
			// default is 'default'
			position: 'border',
			// draw label even it's overlap, default is false
			overlap: true
	}
	$scope.options.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};
	$scope.options.title = {
			text : "",
			display:true,
			position: 'bottom'

	}
	$scope.radarOptions = {}
	$scope.radarOptions.animation = false;
	$scope.radarOptions.title = {
			text : "",
			display:true,
			position: 'top'

	}
	$scope.radarOptions.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};

	$scope.formatOrganizations = function(objArray){
		var stringOut = "";
		for(var i=0;i<objArray.length;i++){
			stringOut += objArray[i].name;
			if(i<objArray.length-1){
				stringOut += ", ";
			}
		}
		return stringOut;
	}

	$scope.newNotificationText = "";
	$scope.sendNotificationModal = function(){
		$("#sendNotificationModal").modal('show');
	}
	$scope.sendNotification = function(){
		server.sendNotification($scope.selectedUser,$scope.newNotificationText);
		$scope.newNotificationText = "";
		$("#sendNotificationModal").modal('hide');
	}
	$scope.$on('userProfile:updated', function(event,data) {
		$scope.user = data;
	});

	$scope.$on('notificationSent:updated', function(event,data) {


	});

	$scope.newUser = {};
	$scope.newUser.username = "";
	$scope.newUser.firstName = "";
	$scope.newUser.lastName = "";
	$scope.newUser.email = "";
	$scope.newUser.country = "";
	$scope.newUser.role = "";
	$scope.newUser.concurrentExercisesLimit = 1;
	$scope.newUser.password = "";
	$scope.newUser.emailVerified = true;
	$scope.newUser.forcePasswordChange = true;
	$scope.newUser.organization = "";

	$scope.availableUserRoles = [{ id:7, name:"User"},{ id:4, name:"Monitor"},{ id:3, name:"Team Manager"},{ id:1, name:"Reviewer"},{ id:0, name: "Admin"}];

	$scope.user = server.user;
	$scope.countries = server.countries;

	$scope.usernameAvailable = true;

	$scope.isUsernameAvailable = function(){
		if($scope.newUser.username != "" )
			server.checkUsernameAvailable($scope.newUser.username);
	}
	$scope.$on('usernameAvailable:updated', function(event,data) {
		$scope.usernameAvailable = data.result;
	});

	$scope.$on('userAdded:updated', function(event,data) {
		server.getUsers();
	});

	$scope.addUserModal = function(){
		$('#addUserModal').modal('show');
	}

	$scope.addUser = function(){
		server.addUser($scope.newUser);
		$('#addUserModal').modal('hide');
		$scope.newUser = {};
		$scope.newUser.firstName = "";
		$scope.newUser.lastName = "";
		$scope.newUser.email = "";
		$scope.newUser.country = "";
		$scope.newUser.role = "";
		$scope.newUser.concurrentExercisesLimit = 1;
		$scope.newUser.password = "";
		$scope.newUser.emailVerified = true;
		$scope.newUser.forcePasswordChange = true;
		$scope.newUser.organization = "";
	}




	$scope.$on('usersList:updated', function(event,data) {
		$scope.masterUsersList = data;
		$scope.filteredUsersList = $filter("filter")($scope.masterUsersList, $scope.query);
	});
	$scope.usertableconfig = {
			itemsPerPage: 20,
			fillLastPage: false
	}
	$scope.passwordResetValue = "";
	$rootScope.showUserDetails = false;
	$rootScope.showUserList = true;
	$scope.selectedUser = "";

	$scope.updateFilteredList = function() {
		$scope.filteredUsersList = $filter("filter")($scope.masterUsersList, $scope.query);
	};

	$scope.removeFromTeam = function(){
		server.removeFromTeam($scope.userDetails.team.id, $scope.userDetails.user)
	}

	$scope.unlockAccount = function(){
		if($scope.selectedUser!="")
			server.unlockUserAccount($scope.selectedUser);
	}
	$scope.resetPassword = function(){
		if($scope.selectedUser!="" && $scope.passwordResetValue!=""){
			server.resetUserPassword($scope.selectedUser,$scope.passwordResetValue);
			$scope.passwordResetValue="";
		}
	}
	$scope.$on('accountUnlocked:updated', function(event,data) {
		if($scope.selectedUser!="")
			$scope.getUserDetails($scope.selectedUser);
		server.getUsers();
	});
	$scope.back = function(){
		window.history.go(-1);
	}
	$scope.displayUserList = function(){
		$rootScope.showUserList = true;
		$rootScope.showUserDetails = false;
		$location.path("users", false);
	}
	$scope.userDetails = {};
	$scope.getUserDetails = function(username){
		server.getUserDetails(username);
		server.getUserStats(username);
		server.getUserExercises(username);
		server.getUserAchievements(username);
		$location.path("users/details/"+username, false);
	}
	$scope.$on('userCompletedExercises:updated', function(event,data) {
		$scope.scorePerExercises.options = cloneObj($scope.options);
		$scope.scorePerExercises.data = [[],[]];
		$scope.scorePerExercises.series = ["Score","Duration (minutes)"];
		$scope.scorePerExercises.labels = [];
		$scope.scorePerExercises.datasetOverride = [ {showLine: true, pointStyle:'rectRounded',fill: false,
			pointRadius: 10,pointHoverRadius: 15},{ showLine: true,pointStyle:'rectRounded',fill: false,
				pointRadius: 10,pointHoverRadius: 15}] ;
		$scope.scorePerExercises.options.events= ["mousemove", "mouseout", "click", "touchstart", "touchmove", "touchend"];
		$scope.scorePerExercises.options.title.display=false;
		$scope.scorePerExercises.options.pieceLabel = {
				// render 'label', 'value', 'percentage' or custom function, default is 'percentage'
				render: 'value',
				// precision for percentage, default is 0
				precision: 0,
				// identifies whether or not labels of value 0 are displayed, default is false
				showZero: true,
				// font size, default is defaultFontSize
				fontSize: 14,
				// font color, can be color array for each data, default is defaultFontColor
				fontColor: '#FFF',
				// font style, default is defaultFontStyle
				fontStyle: 'normal',
				// font family, default is defaultFontFamily
				fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
				// draw label in arc, default is false
				arc: false,
				// position to draw label, available value is 'default', 'border' and 'outside'
				// default is 'default'
				position: 'border',
				// draw label even it's overlap, default is false
				overlap: true
		}
		if(data.result!="error"){
			for (var property in data) {
				if (data.hasOwnProperty(property)) {
					$scope.scorePerExercises.labels.push(moment(data[property].endTime).format('MMM DD'))
					$scope.scorePerExercises.data[0].push(data[property].score.result);
					$scope.scorePerExercises.data[1].push(data[property].duration);

				}
			}
		}
	})
	$scope.$on('statsUser:updated', function(event,data) {

		$scope.remediationRatePerCategory = [];
		for (var property in data.categoriesRemediationRate) {
			if (data.categoriesRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.categoriesRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.categoriesRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerCategory.push(obj);
			}
		}
		$scope.remediatedPerCategory.data = [[]];
		$scope.remediatedPerCategory.labels = [];
		for(var j in $scope.remediationRatePerCategory){
			if ($scope.remediationRatePerCategory.hasOwnProperty(j)){
				var tmpName = $scope.remediationRatePerCategory[j].options.title.text;
				$scope.remediatedPerCategory.labels.push(tmpName);
				$scope.remediatedPerCategory.data[0].push(Math.ceil(data.totalMinutesPerIssueCategory[tmpName]));
			}
		}
		$scope.remediatedPerCategory.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerCategory.options.title.display = false;
		$scope.remediatedPerCategory.series = ["Total Time (minutes)"];


	});

	$scope.getDateFormat = function(date){
		if(undefined==date || moment(date)._d=="Invalid Date")
			return "Not Available";
		else
			return moment(date).format("D MMMM YYYY");	
	}
	$scope.trophies = [];
	$scope.score = 0;
	$scope.$on('userAchievements:updated', function(event,data) {
		$scope.score = "N/A";
		$scope.trophies = [];
		if(undefined!=data.score){
			$scope.score = data.score;
		}
		if(undefined!=data.trophies){
			var trophiesArray = deepCopy(data.trophies);
			var size = 4;
			while (trophiesArray.length > 0){
				$scope.trophies.push(trophiesArray.splice(0, size));
			}
		}
	});

	$scope.$on('userDetails:updated', function(event,data) {
		switch(data.r){
		case 0:
			data.role = "Organization Admin";
			break;
		case 1:
			data.role = "Organization Reviewer";
			break;
		case 3:
			data.role = "Team Manager";
			break;
		case 4:
			data.role = "Organization Stats";
			break;
		case 7:
			data.role = "User";
			break;
		default:
			data.role = "N/A";
		}
		$scope.selectedUser = data.user;
		$scope.userDetails = data;
		$rootScope.showUserList = false;
		$rootScope.showUserDetails = true;
	});
}])
rtf.controller('availableExercises',['$scope','server','$rootScope','$location','$filter','notificationService',function($scope,server,$rootScope,$location,$filter,notificationService){

	$scope.selectedExercises = "";
	$scope.filteredAvailableExercisesList = [];
	$scope.user = server.user;
	$scope.masterAvailableExercisesList = [];
	$scope.exercisesForOrgs = [];
	$scope.availableRegions = [];
	$rootScope.showExerciseList = true;
	$rootScope.showExerciseDetails = false;
	$scope.showList = true;
	$scope.showDetails = false;
	$scope.getExerciseDetails = function(exId){
		server.getExerciseDetails(exId);
		server.getRegionsForExercise(exId);
	}

	$scope.enableExerciseForOrg = function(idExercise,idOrg){
		server.enableExerciseForOrg(idExercise,idOrg);
	}
	$scope.disableExerciseForOrg = function(idExercise,idOrg){
		server.disableExerciseForOrg(idExercise,idOrg);
	}

	$scope.$on('disableExerciseForOrg:updated', function(event,data) {
		server.getAvailableExercises();
		notificationService.success("Exercise successfully disabled.")
	});
	$scope.$on('enableExerciseForOrg:updated', function(event,data) {
		server.getAvailableExercises();
		notificationService.success("Exercise successfully enabled.")
	});

	$scope.$on('exerciseRegions:updated', function(event,data) {
		if(data!=null){
			$scope.availableRegions = [];
			for(var j in data){
				if(!Number.isInteger(parseInt(j)))
					continue;
				var region = "Unavailable";

				switch(data[j]) {
				case "EU_WEST_1":
					region = "Emea";
					break;
				case "US_EAST_1":
					region = "North America"
						break;
				case "AP_SOUTH_1":
					region = "Apac - India"
						break;
				case "AP_SOUTHEAST_1":
					region = "Apac - Singapore"
						break;
				default:
					break;
				}
				$scope.availableRegions.push(region);
			}
		}
	});
	$scope.downloadAPIReference = function(id){
		server.downloadExerciseReference(id);
	}
	$scope.downloadSolutions = function(id){
		server.downloadExerciseSolutions(id);
	}

	$rootScope.exerciseDetails = [];
	$scope.$on('exerciseDetails:updated', function(event,data) {
		data.res = [];
		for (var property in data.resources) {
			if (data.resources.hasOwnProperty(property)) {
				var tmpObj = {};
				tmpObj.name = property
				tmpObj.url = data.resources[property];
				data.res.push(tmpObj);
			}
		}
		$rootScope.exerciseDetails = data;
		$rootScope.showExerciseDetails = true;
		$rootScope.showExerciseList = false;
		$location.path("available-exercises/details/"+$rootScope.exerciseDetails.id, false);
	});

	$scope.backToList = function(){
		$rootScope.showExerciseDetails = false;
		$rootScope.showExerciseList = true;
	}


	$scope.getExerciseStatusString = function(status){
		switch(status){
		case "0":
			return "Available";
			break;
		case "1":
			return "Updated";
			break;
		case "2":
			return "Coming Soon";
			break;
		case "3":
			return "Inactive";
			break;
		default:
			return "N/A";
		}
	}


	$scope.updateAvailableExercisesFilteredList = function() {
		$scope.filteredTeamsList = $filter("filter")($scope.masterAvailableExercisesList, $scope.queryAvailableExercises);
	};
	$scope.availableExercisestableconfig = {
			itemsPerPage: 20,
			fillLastPage: false
	}
	$scope.$on('availableExercises:updated', function(event,data) {
		$scope.masterAvailableExercisesList = data.exercises;
		$scope.exercisesForOrgs = data.orgs;
		$scope.filteredAvailableExercisesList = $scope.masterAvailableExercisesList;
	});

	$scope.getExerciseEnabledForOrgText = function(orgId,exId){
		if(undefined == orgId || undefined == exId)
			return false;
		for(var i in $scope.exercisesForOrgs){
			if ($scope.exercisesForOrgs[i].hasOwnProperty("organization") && $scope.exercisesForOrgs[i].organization.id==orgId){
				for(var j in $scope.exercisesForOrgs[i].exercises){
					if($scope.exercisesForOrgs[i].exercises[j].id==exId){
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

}]);

rtf.controller('teams',['$scope','server','$rootScope','$location','$filter','notificationService',function($scope,server,$rootScope,$location,$filter,notificationService){
	$rootScope.showTeamMembers = false;
	$rootScope.showTeamList = true;
	$rootScope.selectedTeam = "";
	$scope.selectedTeamManagers = [];
	$scope.filteredTeamsList = [];
	$scope.masterTeamsList = [];
	$scope.masterMembersList = [];
	$scope.filteredMembersList = [];

	$scope.userObj = {};
	$scope.selectedUsersList = [];
	$scope.userCheckToggle=function(s){
		if($scope.selectedUsersList.indexOf(s)<0){
			$scope.selectedUsersList.push(s);
		}
		else{
			var idx = $scope.selectedUsersList.indexOf(s);
			$scope.selectedUsersList.remove(idx,idx);
		}
	}
	//addusertoteam
	$scope.addToTeamModal = function(){
		$('#addUsersToTeamModal').modal('show');
		server.getAvailableUsersForTeam($rootScope.selectedTeam);
	}

	$scope.$on('availableUsersTeam:updated', function(event,data) {
		$scope.availableUsersList = data;
	})

	$scope.renameTeam = function(teamId){
		server.renameTeam(teamId,$scope.renamedTeamName);
		$scope.renamedTeamName = "";
	}
	$scope.$on('teamRenamed:updated', function(event,data) {
		$('#renameTeamModal').modal('hide');
		notificationService.success('Team renamed.');
		server.getTeams();
	})
	$scope.teamToRename = {};
	$scope.renameTeamModal = function(id,name){
		$scope.teamToRename = {};
		$scope.teamToRename.id = id;
		$scope.teamToRename.name = name;
		$('#renameTeamModal').modal('show');
	}

	$scope.addUsersToTeam = function(){
		server.addUsersToTeam($scope.selectedUsersList, $rootScope.selectedTeam);
		$scope.selectedUsersList = [];
	}
	$scope.$on('usersAddedToTeam:updated', function(event,data) {
		if(data.result!="error"){
			$scope.getTeamMembers($rootScope.selectedTeam);
			server.getPendingReviews();
			server.getCompletedReviews();
			$('#addUsersToTeamModal').modal('hide');
		}

	})

	//add team
	$scope.user = server.user;
	$scope.newTeamName = "";
	$scope.newTeamOrganization = "";

	$scope.removeFromTeam = function(teamId, username){
		server.removeFromTeam(teamId, username);
		$('#removeFromTeamModal').modal('hide');
	}
	$scope.userToRemove = {};
	$scope.removeFromTeamModal = function(team, username){
		$scope.userToRemove = {}
		$scope.userToRemove.teamName = team.name;
		$scope.userToRemove.teamId = team.id;
		$scope.userToRemove.username = username;
		$('#removeFromTeamModal').modal('show');
	}
	$scope.$on('deletedFromTeam:updated', function(event,data) {
		if(data.result=="success"){
			if($rootScope.visibility.teams)
				$scope.getTeamMembers($rootScope.selectedTeam)
			server.getPendingReviews();
			server.getCompletedReviews();
		}
	})

	$scope.removeTeamManagerModal = function(teamId, username){
		$scope.userToRemove= {};
		$scope.userToRemove.teamId = teamId;
		$scope.userToRemove.username = username;
		$('#deleteTeamManagerModal').modal('show');
	}
	$scope.removeTeamManager = function(teamId, username){
		$('#deleteTeamManagerModal').modal('hide');
		server.removeTeamManager(teamId, username)
	}
	$scope.$on('removeTeamManager:updated', function(event,data) {
		if(data.result=="success")
			server.getTeamDetails($rootScope.selectedTeam);
	})

	$scope.makeTeamManager = function(teamId, username){
		server.makeTeamManager(teamId, username)
	}
	$scope.$on('makeTeamManager:updated', function(event,data) {
		if(data.result=="success")
			server.getTeamDetails($rootScope.selectedTeam);
	})
	$scope.teamToDelete = {};
	$scope.deleteTeamModal = function(teamId,teamName){
		$scope.teamToDelete = {};
		$scope.teamToDelete.id = teamId;
		$scope.teamToDelete.name = teamName;
		$('#deleteTeamModal').modal('show');
	}
	$scope.deleteTeam = function(teamId){
		server.deleteTeam(teamId);
		$('#deleteTeamModal').modal('hide');
	}
	$scope.saveNewTeam = function(){
		if($scope.newTeamOrganization!="" && $scope.newTeamName!=""){
			server.addTeam($scope.newTeamName,$scope.newTeamOrganization);
			$('#newTeamModal').modal('hide');
			$scope.newTeamName = "";
			$scope.newTeamOrganization = "";
		}
	}
	$scope.$on('addTeam:updated', function(event,data) {
		if(data.result=="success")
			server.getTeams();
	})
	$scope.$on('deletedTeam:updated', function(event,data) {
		if(data.result=="success")
			server.getTeams();
	})
	$scope.$on('teamNameAvailable:updated', function(event,data) {
		$scope.teamNameAvailable = data.result;
	});
	$scope.teamNameAvailable = true;

	$scope.isRenamedTeamNameAvailable = function(){
		if($scope.renamedTeamName != "" )
			server.isTeamNameAvailable($scope.renamedTeamName);
	}
	$scope.isTeamNameAvailable = function(){
		if($scope.newTeamName != "" )
			server.isTeamNameAvailable($scope.newTeamName);
	}

	$scope.addNewTeamModal = function(){
		$('#newTeamModal').modal('show');
	}
	$scope.remediatedPerIssue = {}
	$scope.remediatedPerIssue.data = [];
	$scope.remediatedPerIssue.labels = [];
	$scope.remediatedPerCategory = {}
	$scope.remediatedPerCategory.data = [];
	$scope.remediatedPerCategory.labels = [];
	$scope.remediationRatePerIssue = [];
	$scope.remediationRatePerCategory = [];
	$scope.scorePerExercises = {};
	$scope.options = {}
	$scope.options.animation = false;
	$scope.options.layout = {
			padding: {
				left: 0,
				right: 0,
				top: 0,
				bottom: 0
			}
	}
	$scope.options.pieceLabel = {
			// render 'label', 'value', 'percentage' or custom function, default is 'percentage'
			render: 'percentage',
			// precision for percentage, default is 0
			precision: 0,
			// identifies whether or not labels of value 0 are displayed, default is false
			showZero: false,
			// font size, default is defaultFontSize
			fontSize: 12,
			// font color, can be color array for each data, default is defaultFontColor
			fontColor: '#FFF',
			// font style, default is defaultFontStyle
			fontStyle: 'normal',
			// font family, default is defaultFontFamily
			fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
			// draw label in arc, default is false
			arc: false,
			// position to draw label, available value is 'default', 'border' and 'outside'
			// default is 'default'
			position: 'border',
			// draw label even it's overlap, default is false
			overlap: true
	}
	$scope.options.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};
	$scope.options.title = {
			text : "",
			display:true,
			position: 'bottom'

	}
	$scope.radarOptions = {}
	$scope.radarOptions.animation = false;
	$scope.radarOptions.title = {
			text : "",
			display:true,
			position: 'top'

	}
	$scope.radarOptions.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};

	$scope.updateTeamsFilteredList = function() {
		$scope.filteredTeamsList = $filter("filter")($scope.masterTeamsList, $scope.queryTeams);
	};


	$scope.updateMembersFilteredList = function() {
		$scope.filteredMembersList = $filter("filter")($scope.masterMembersList, $scope.queryMembers);
	};

	$scope.teamstableconfig = {
			itemsPerPage: 20,
			fillLastPage: false
	}
	$scope.$on('teamsList:updated', function(event,data) {
		$scope.masterTeamsList = data;
		$scope.filteredTeamsList = $scope.masterTeamsList;
	});
	$scope.isTeamManager = function(idUser){
		return $scope.selectedTeamManagers.indexOf(idUser)>=0;
	}
	$scope.displayTeamList = function(){
		$rootScope.showTeamMembers = false;
		$rootScope.showTeamList = true;
		$location.path("teams", false);
	}
	$scope.teamMembersData = [];
	$scope.getTeamMembers = function(name){
		$location.path("teams/details/"+name, false);
		$rootScope.selectedTeam = name;
		server.getTeamMembers(name);
		server.getTeamStats(name);
		server.getTeamDetails(name);
	};
	$scope.$on('teamDetails:updated', function(event,data) {
		$scope.selectedTeamManagers = [];
		for(var i=0; i<data.managers.length;i++){
			$scope.selectedTeamManagers.push(data.managers[i]['idUser']);
		}
	});
	$scope.$on('statsTeam:updated', function(event,data) {
		$scope.remediationRatePerIssue = [];
		for (var property in data.issuesRemediationRate) {
			if (data.issuesRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.issuesRemediationRate[property]).length>0) {

				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.issuesRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerIssue.push(obj);
			}
		}
		$scope.remediatedPerIssue.data = [[]];
		$scope.remediatedPerIssue.labels = [];
		for(var j in $scope.remediationRatePerIssue){
			if ($scope.remediationRatePerIssue.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerIssue[j].data[0]
				var tmpTot = $scope.remediationRatePerIssue[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerIssue[j].options.title.text
				$scope.remediatedPerIssue.labels.push(tmpName);
				$scope.remediatedPerIssue.data[0].push(tmpPercentage);
			}
		}
		$scope.remediatedPerIssue.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerIssue.options.title.display = false;
		$scope.remediatedPerIssue.series = ["Remediated (%)"];

		$scope.remediationRatePerCategory = [];
		for (var property in data.categoriesRemediationRate) {
			if (data.categoriesRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.categoriesRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.categoriesRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerCategory.push(obj);
			}
		}
		$scope.remediatedPerCategory.data = [[],[],[]];
		$scope.remediatedPerCategory.labels = [];
		for(var j in $scope.remediationRatePerCategory){
			if ($scope.remediationRatePerCategory.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerCategory[j].data[0]
				var tmpTot = $scope.remediationRatePerCategory[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerCategory[j].options.title.text;
				$scope.remediatedPerCategory.labels.push(tmpName);
				$scope.remediatedPerCategory.data[0].push(tmpPercentage);
				$scope.remediatedPerCategory.data[1].push(Math.ceil(data.totalMinutesPerIssueCategory[tmpName]/60));
				$scope.remediatedPerCategory.data[2].push(data.avgMinutesPerIssueCategory[tmpName]);
			}
		}
		$scope.remediatedPerCategory.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerCategory.options.title.display = false;
		$scope.remediatedPerCategory.series = ["Remediated (%)","Total Time (hours)","Avg Time (minutes)"];

	});
	$scope.$on('teamMembers:updated', function(event,data) {
		$scope.masterMembersList = data;
		$scope.filteredMembersList = $scope.masterMembersList;
		$rootScope.showTeamList = false;
		$rootScope.showTeamMembers = true;
	});
}])
rtf.controller('review',['$scope','server','$rootScope','$filter',function($scope,server,$rootScope,$filter){
	$scope.user = server.user;
	$scope.showCodeDiff = false;
	$scope.showLogs = false;
	$scope.showResults = false;
	$scope.selectedResultRow = -1;
	$rootScope.masterPendingReviews = []
	$scope.pendingtableconfig = {
			itemsPerPage: 10,
			fillLastPage: false
	}
	$scope.emptyDiff = false;
	$scope.emptyLog = false;
	$scope.zipError = false;
	$scope.filteredPendingList = []; 

	$scope.updateFilteredList = function() {
		$scope.filteredPendingList = $filter("filter")($rootScope.masterPendingReviews, $scope.query);
	};

	$scope.assessorScore = 0;
	$scope.partialScores = {};
	$scope.assessorComments = {};
	$scope.newIssuesIntroducedDetails = "";
	$scope.awardTrophy = false;
	$scope.newIssuesIntroduced = false;
	$scope.$watchCollection('partialScores', function(newVal, oldVal){
		$scope.assessorScore = 0;
		for(var i in $scope.partialScores){
			if(Number.isInteger(parseInt($scope.partialScores[i])))
				$scope.assessorScore += parseInt($scope.partialScores[i]);
		}
	}, true);

	$scope.vulnerabilityStatus = [{id:0, name:"Not Vulnerable"},{id:1, name:"Vulnerable"},{id:2, name:"Broken Functionality"},{id:4, name:"Not Addressed"},{id:3, name:"N/A"}]

	$scope.pendingItemDetails = {};
	$scope.assessorStatus = {};

	$scope.markCancelled = function(){
		server.markCancelled($scope.pendingItemDetails.id);
	}

	$scope.submitReview = function(){
		var obj = {};
		obj.review = [];

		for (var property in $scope.partialScores) {
			if ($scope.partialScores.hasOwnProperty(property)) {
				var tmpObj = {};
				tmpObj.name = property;
				tmpObj.status = $scope.assessorStatus[property].id;
				tmpObj.score = $scope.partialScores[property];
				tmpObj.verified = true;
				tmpObj.comment = $scope.assessorComments[property];
				obj.review.push(tmpObj);
			}
		}

		obj.id = $scope.pendingItemDetails.id;
		obj.totalScore = $scope.assessorScore;
		obj.awardTrophy = $scope.awardTrophy;
		obj.newIssuesIntroduced = $scope.newIssuesIntroduced;
		obj.newIssuesIntroducedText = $scope.newIssuesIntroducedDetails;

		server.submitReview(obj);
	}


	$scope.size = function(obj) {
		var size = 0, key;
		for (key in obj) {
			if (obj.hasOwnProperty(key)) size++;
		}
		return size;
	};

	$scope.$on('pendingReviews:updated', function(event,data) {
		$rootScope.masterPendingReviews = data;
		$scope.filteredPendingList = $filter("filter")($rootScope.masterPendingReviews, $scope.query);
		$scope.showResults = false;
	});
	$scope.$on('reviewSubmitted:updated', function(event,data) {
		server.getPendingReviews();
		server.getCompletedReviews();
		server.getUsers();
	});
	$scope.$on('reviewCancelled:updated', function(event,data) {
		server.getPendingReviews();
		server.getCompletedReviews();
	});
	var remediationClassMap = {
			"Remediated": "table-success",
			"Vulnerable": "table-danger",
			"Broken Functionality":"table-warning"
	}	
	$scope.getRemedationTableClass = function(status) {
		return remediationClassMap[status]
	};
	var statusClassMap = {
			"0": "table-success",
			"1": "table-danger",
			"2": "table-warning",
			"4": "table-info"
	}
	$scope.getStatusString = function(status){
		switch(status){
		case "1":
			return "Vulnerable"
		case "0":
			return "Not Vulnerable"
		case "2":
			return "Broken Functionality"
		case "3":
			return "N/A"
		case "4":
			return "Not Addressed"

		default: return status;
		}
	}
	$scope.getStatusClass = function(status) {
		return statusClassMap[status]
	};
	var flagTypeMap = {
			"EXPLOITATION": "table-info",
			"REMEDIATION": "table-warning"
	}
	$scope.getClassForFlagType = function(type){
		return flagTypeMap[type]
	};
	var scoreClassMap = {
			success: "success",
			average: "warning",
			failure: "danger",
			pending: "info"
	};
	$scope.getDurationInterval = function(start,end){
		var out = moment.utc(moment(end).diff(moment(start))).format("H mm").replace(" ","h")+"'";
		return out;

	};
	$scope.getMinutesDuration = function(dur){
		if(undefined != dur && null != dur && 0 != dur){
			return moment.utc(moment.duration(dur,"m").asMilliseconds()).format("H mm").replace(" ","h")+"'";
		}
		else{
			return "N/A"
		}
	};
	$scope.getDatesInterval = function(start,end){
		var out = moment(start).format("MMM D YYYY, HH:mm");
		out += " - "+moment(end).format("HH:mm");
		return out;
	}

	$scope.getResultsScoreClass = function(result,total) {
		if(result==-1)
			return scoreClassMap["pending"];
		if(result==total)
			return scoreClassMap["success"];
		else if(result<(total/3))
			return scoreClassMap["failure"];
		else  
			return scoreClassMap["average"];
	};

	$scope.toggleCodeDiff = function(){
		if($scope.showCodeDiff == true)
			$scope.showCodeDiff = false;
		else{
			$scope.showLogs = false;
			$scope.showCodeDiff = true;
		}
	}
	$scope.toggleInstanceLogs = function(){
		if($scope.showLogs == true)
			$scope.showLogs = false;
		else{
			$scope.showCodeDiff = false;
			$scope.showLogs = true;
		}
	}



	$scope.$on('pendingReviewDetails:updated', function(event,data) {

		var offset = $('#showResults').offset();
		$('html, body').animate({
			scrollTop: offset.top - 50,
			scrollLeft: offset.left
		});

		for(var i in data.results){
			switch(data.results[i].status){
			case "VULNERABLE":
				data.results[i].status = "Vulnerable"
					break;
			case "NOT_VULNERABLE":
				data.results[i].status = "Not Vulnerable"
					break;
			case "BROKEN_FUNCTIONALITY":
				data.results[i].status = "Broken Functionality"
					break;
			case "NOT_AVAILABLE":
				data.results[i].status = "N/A"
					break;
			case "NOT_ADDRESSED":
				data.results[i].status = "Not Addressed"
					break;
			}
		}
		if(data.results.length == 0){
			data.results = [];
			for(var j in data.exercise.flags){
				data.results[j] = {};
				data.results[j].name = data.exercise.flags[j].title;
				data.results[j].status = "N/A";
				data.results[j].verified = false; 
			}
		}
		$scope.showCodeDiff = false;
		$scope.showLogs = false;
		$scope.pendingItemDetails = data;
		$scope.emptyDiff = false;
		$scope.emptyLog = false;
		$scope.zipError = false;

		$scope.showResults = true;



		JSZipUtils.getBinaryContent($scope.pendingItemDetails.id,$rootScope.ctoken,'/management/team/handler','getReviewFile', function(err, data) {
			if(err) {
				throw err; // or handle err
				$scope.zipError = true;
			}

			JSZip.loadAsync(data).then(function(zip) {
				for(file in zip.files){
					if(file.indexOf('sourceDiff.txt')>-1){
						zip.file(file).async("string").then(function success(content) {
							var diffString = content;
							if(diffString==""){
								$scope.emptyDiff = true;
								$('#targetDiv').empty()
								return;
							}
							try{
								var diff2htmlUi = new Diff2HtmlUI({diff : diffString });
								diff2htmlUi.draw( '#targetDiv', {
									inputFormat : 'diff',
									showFiles : true,
									matching : 'lines'
								});
								diff2htmlUi.highlightCode('#targetDiv');
							} catch(err){
							}
						})                        
					}
					else if(file.indexOf('rtf.log')>-1){
						zip.file(file).async("string").then(function success(content) {
							var resultString = content;
							if(resultString==""){
								$scope.emptyLog = true;
								$('#rtfLogText').empty()
								return;
							}
							rtfLog = resultString;
							var datas = rtfLog.split("\n");
							var tba = "";
							for (var i = 0; i < datas.length; i++) {
								if (datas[i] !== "") {
									tba += '<li class="list-group-item">' + htmlEncode(datas[i]) + '</li>';
								}
							}
							$('#rtfLogText').append(tba);
							var diff2htmlUi = new Diff2HtmlUI();
							diff2htmlUi.highlightCode('#rtfLogText');
							diff2htmlUi.highlightCode('.list-group-item');
						});   
					}
				}
			});
		});    
	})

	$scope.showDetailsFor = function(eId, index){
		$scope.assessorStatus = {};
		$scope.assessorScore = 0;
		$scope.partialScores = {};
		$scope.assessorComments = {};
		$scope.reviewForm.$setPristine();
		server.getPendingReviewDetails(eId);
		$scope.selectedResultRow = index;
	};
}]);
rtf.controller('stats',['$scope','server',function($scope,server){

	$scope.user = server.user;

	$scope.orgFilter = [];

	$scope.refreshStatsFilter = function(){
		var filter = [];
		for(var i=0; i< $scope.orgFilter.length; i++){
			if($scope.orgFilter[i].checked)
				filter.push($scope.orgFilter[i].id);
		}
		server.getGlobalStats(filter);
	}

	$scope.$on('userProfile:updated', function(event,data) {
		$scope.orgFilter = [];
		for(var i=0;i<$scope.user.organizations.length;i++){
			var tmpObj = cloneObj($scope.user.organizations[i]);
			tmpObj.checked = true;
			$scope.orgFilter.push(tmpObj);
		}
	});


	$scope.charts = {};
	$scope.charts.dashboard = true;
	$scope.charts.remediationRateIssue = false;
	$scope.charts.remediationRateTeam = false;
	$scope.charts.remediationRateCategory  = false;
	$scope.charts.remediationRateRegion  = false;

	$scope.remediatedPerIssue = {}
	$scope.remediatedPerIssue.data = [];
	$scope.remediatedPerIssue.labels = [];

	$scope.remediatedPerCategory = {}
	$scope.remediatedPerCategory.data = [];
	$scope.remediatedPerCategory.labels = [];

	$scope.remediatedPerTeam = {}
	$scope.remediatedPerTeam.data = [];
	$scope.remediatedPerTeam.labels = [];


	$scope.remediatedPerRegion = {}
	$scope.remediatedPerRegion.data = [];
	$scope.remediatedPerRegion.labels = [];

	$scope.remediationRatePerIssue = [];
	$scope.remediationRatePerCategory = [];
	$scope.remediationRatePerRegion = [];
	$scope.remediationRatePerTeam = [];

	$scope.showStats = function(chart){
		if(undefined!=$scope.charts[chart]){
			$('.waitLoader').show();
			for(var i in $scope.charts){
				if($scope.charts.hasOwnProperty(i)){
					$scope.charts[i] = false;
				}
			}
			$scope.charts[chart] = true;
			$('.waitLoader').fadeOut(1000);
		}
	}

	$scope.options = {}
	$scope.options.animation = false;
	$scope.options.layout = {
			padding: {
				left: 0,
				right: 0,
				top: 0,
				bottom: 0
			}
	}
	$scope.options.pieceLabel = {
			// render 'label', 'value', 'percentage' or custom function, default is 'percentage'
			render: 'percentage',
			// precision for percentage, default is 0
			precision: 0,
			// identifies whether or not labels of value 0 are displayed, default is false
			showZero: false,
			// font size, default is defaultFontSize
			fontSize: 12,
			// font color, can be color array for each data, default is defaultFontColor
			fontColor: '#FFF',
			// font style, default is defaultFontStyle
			fontStyle: 'normal',
			// font family, default is defaultFontFamily
			fontFamily: "'Helvetica Neue', 'Helvetica', 'Arial', sans-serif",
			// draw label in arc, default is false
			arc: false,
			// position to draw label, available value is 'default', 'border' and 'outside'
			// default is 'default'
			position: 'border',
			// draw label even it's overlap, default is false
			overlap: true
	}
	$scope.options.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};
	$scope.options.title = {
			text : "",
			display:true,
			position: 'bottom'

	}
	$scope.radarOptions = {}
	$scope.radarOptions.title = {
			text : "",
			display:true,
			position: 'top'

	}
	$scope.radarOptions.legend = {
			display: true,
			position: 'bottom',
			labels: {
				fontColor: "#000",
				fontSize: 10
			}
	};

	$scope.$on('stats:updated', function(event,data) {

		$scope.statsObj = data;

		$scope.remediationRatePerTeam = [];
		for (var property in data.teamRemediationRate) {
			if (data.teamRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.teamRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.teamRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerTeam.push(obj);
			}
		}
		$scope.remediatedPerTeam.data = [[],[],[]];
		$scope.remediatedPerTeam.labels = [];
		for(var j in $scope.remediationRatePerTeam){
			if ($scope.remediationRatePerTeam.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerTeam[j].data[0]
				var tmpTot = $scope.remediationRatePerTeam[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerTeam[j].options.title.text;
				$scope.remediatedPerTeam.labels.push(tmpName);
				$scope.remediatedPerTeam.data[0].push(tmpPercentage);
				$scope.remediatedPerTeam.data[1].push((Math.ceil(data.totalMinutesPerTeam[tmpName]/60)));
				$scope.remediatedPerTeam.data[2].push(data.avgMinutesPerTeam[tmpName]);

			}
		}
		$scope.remediatedPerTeam.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerTeam.options.title.display = false;
		$scope.remediatedPerTeam.series = ["Remediated (%)","Total Time (hours)","Avg Time (minutes)"];

		$scope.remediationRatePerIssue = [];
		for (var property in data.issuesRemediationRate) {
			if (data.issuesRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.issuesRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.issuesRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerIssue.push(obj);
			}
		}
		$scope.remediatedPerIssue.data = [[]];
		$scope.remediatedPerIssue.labels = [];
		for(var j in $scope.remediationRatePerIssue){
			if ($scope.remediationRatePerIssue.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerIssue[j].data[0]
				var tmpTot = $scope.remediationRatePerIssue[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerIssue[j].options.title.text
				$scope.remediatedPerIssue.labels.push(tmpName);
				$scope.remediatedPerIssue.data[0].push(tmpPercentage);
			}
		}
		$scope.remediatedPerIssue.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerIssue.options.title.display = false;
		$scope.remediatedPerIssue.series = ["Remediated (%)"];

		$scope.remediationRatePerCategory = [];
		for (var property in data.categoriesRemediationRate) {
			if (data.categoriesRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.categoriesRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				obj.options.title.text = property;
				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.categoriesRemediationRate[property][tmpStatus]
					if(undefined==tmpValue){
						tmpValue = 0;
					}
					obj.data.push(tmpValue);
				}
				$scope.remediationRatePerCategory.push(obj);
			}
		}
		$scope.remediatedPerCategory.data = [[],[],[]]
		$scope.remediatedPerCategory.labels = [];
		for(var j in $scope.remediationRatePerCategory){
			if ($scope.remediationRatePerCategory.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerCategory[j].data[0]
				var tmpTot = $scope.remediationRatePerCategory[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerCategory[j].options.title.text;
				$scope.remediatedPerCategory.labels.push(tmpName);
				$scope.remediatedPerCategory.data[0].push(tmpPercentage);
				$scope.remediatedPerCategory.data[1].push(Math.ceil(data.totalMinutesPerIssueCategory[tmpName]/60));
				$scope.remediatedPerCategory.data[2].push(data.avgMinutesPerIssueCategory[tmpName]);
			}
		}
		$scope.remediatedPerCategory.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerCategory.options.title.display = false;
		$scope.remediatedPerCategory.series = ["Remediated (%)","Total Time (hours)","Avg Time (minutes)"];

		$scope.remediationRatePerRegion = [];
		for (var property in data.regionsRemediationRate) {
			if (data.regionsRemediationRate.hasOwnProperty(property) && 
					Object.keys(data.regionsRemediationRate[property]).length>0) {
				var obj = {};
				obj.options = cloneObj($scope.options);
				switch(property) {
				case "EU_WEST_1":
					obj.options.title.text = "Europe, Middle East & Africa";
					break;
				case "US_EAST_1":
					obj.options.title.text = "Americas"
						break;
				case "AP_SOUTHEAST_1":
					obj.options.title.text = "Asia Pacific"
						break;
				default:{
					obj.options.title.text = property.replaceAll("_"," ");
					break;
				}
				}

				obj.data = [];
				obj.labels = ["Not Vulnerable", "Vulnerable", "Not Addressed", "Broken Functionality"];
				for(var l=0;l<obj.labels.length;l++){
					var tmpStatus = obj.labels[l].toUpperCase().replace(" ","_");
					var tmpValue = data.regionsRemediationRate[property][tmpStatus]
					if(undefined!=tmpValue){
						obj.data.push(tmpValue)
					}
					else{
						obj.data.push(0)
					}
				}
				$scope.remediationRatePerRegion.push(obj);
			}
		}
		$scope.remediatedPerRegion.labels = [];
		$scope.remediatedPerRegion.data = [[],[],[]]
		for(var j in $scope.remediationRatePerRegion){
			if ($scope.remediationRatePerRegion.hasOwnProperty(j)){
				var tmpRem = $scope.remediationRatePerRegion[j].data[0]
				var tmpTot = $scope.remediationRatePerRegion[j].data.reduce(getSum);
				var tmpPercentage =  Math.floor((tmpRem * 100) / tmpTot);
				var tmpName = $scope.remediationRatePerRegion[j].options.title.text.replaceAll(" ","_");
				$scope.remediatedPerRegion.labels.push($scope.remediationRatePerRegion[j].options.title.text);
				$scope.remediatedPerRegion.data[0].push(tmpPercentage);
				$scope.remediatedPerRegion.data[1].push(Math.ceil(data.totalMinutesPerRegion[tmpName]/60));
				$scope.remediatedPerRegion.data[2].push(data.avgMinutesPerRegion[tmpName]);
			}
		}
		$scope.remediatedPerRegion.options = cloneObj($scope.radarOptions);
		$scope.remediatedPerRegion.options.title.display = false;
		$scope.remediatedPerRegion.series = ["Remediated (%)","Total Time (hours)","Avg Time (minutes)"];

	});

}]);
rtf.controller('settings',['$scope','server','$timeout',function($scope,server,$timeout){
	$scope.user = server.user;
	$scope.countries = server.countries;
	$scope.oldPassword = "";
	$scope.newPasswordRepeat = "";
	$scope.newPassword = "";
	$scope.updateUserProfile = function(){
		server.updateUserProfile($scope.user);
	}
	$scope.updateUserPassword = function(){
		server.updateUserPassword($scope.userPasswordForm.oldPassword.$modelValue, $scope.userPasswordForm.newPassword.$modelValue);
		$scope.oldPassword = "";
		$scope.newPasswordRepeat = "";
		$scope.newPassword = "";
	}
}]);
rtf.controller('challenges',['$scope','server','$rootScope','$location','$filter',function($scope,server,$rootScope,$location,$filter){

	$scope.selectedChallenge = "";
	$scope.filteredChallengesList = [];
	$scope.masterChallengesList = [];
	$rootScope.showChallengesList = true;
	$rootScope.showChallengeDetails = false;
	$scope.showList = true;
	$scope.showDetails = false;
	$scope.getChallengeDetails = function(exId){
		server.getChallengeDetails(exId);
	}

	$rootScope.challengeDetails = [];
	$scope.$on('challengeDetails:updated', function(event,data) {
		data.flags = [];
		data.theads = [];

		for (var property in data.exercises) {
			if (data.exercises.hasOwnProperty(property)) {
				for(var f in data.exercises[property].flags){
					if (data.exercises[property].flags.hasOwnProperty(f)) {
						var tmpObj = {};
						tmpObj.id = data.exercises[property].flags[f].id
						tmpObj.name = data.exercises[property].flags[f].title
						data.theads.push(tmpObj)
						data.flags.push(data.exercises[property].flags[f]);
					}
				}
			}
		}

		var remediated = 0; 
		var runFlags = 0;
		for(var i=0;i<data.runExercises.length;i++){
			for(var j=0;j<data.runExercises[i].results.length;j++){
				runFlags++;
				if(data.runExercises[i].results[j].status == "0"){
					remediated++;
				}
			}
		}

		data.remediation = remediated/runFlags * 100;
		$scope.challengeTableConfig = {
				itemsPerPage: 20,
				fillLastPage: false
		}
		$rootScope.challengeDetails = data;
		for(var u in $rootScope.challengeDetails.users){
			if ($rootScope.challengeDetails.users.hasOwnProperty(u)){
				$rootScope.challengeDetails.users[u].runExercises = 0;
				$rootScope.challengeDetails.users[u].score = 0;
				for(var e in $rootScope.challengeDetails.runExercises){
					if($rootScope.challengeDetails.runExercises.hasOwnProperty(e) && $rootScope.challengeDetails.runExercises[e].user.user==$rootScope.challengeDetails.users[u].user){
						for(var r in $rootScope.challengeDetails.runExercises[e].results){
							if($rootScope.challengeDetails.runExercises[e].results.hasOwnProperty(r)){
								if($rootScope.challengeDetails.runExercises[e].results[r].status=="0"){
									$rootScope.challengeDetails.users[u].score++;
								}
								$rootScope.challengeDetails.users[u].runExercises++;
							} 
						}
					}
				}
			}
		}
		$rootScope.showChallengeDetails = true;
		$rootScope.showChallengesList = false;
		$location.path("challenges/details/"+$rootScope.challengeDetails.id, false);
	});

	$scope.getExerciseIdForFlag = function(flag){
		var runExercises = $rootScope.challengeDetails.runExercises;
		for (var ex=0;ex<runExercises.length;ex++) {
			for(var res=0;res<runExercises[ex]["results"].length;res++){
				if (runExercises[ex]["results"][res].name==flag) {
					return runExercises[ex].id
				}
			}			
		}
	}

	$scope.getChallengeResultFor = function(usr,flag){
		var runExercises = $rootScope.challengeDetails.runExercises;
		var status = "-1";
		//loop1:
		for (var ex=0;ex<runExercises.length;ex++) {
			if(runExercises[ex]["user"].user==usr) {
				for(var res=0;res<runExercises[ex]["results"].length;res++){
					if (runExercises[ex]["results"][res].name==flag) {
						status = runExercises[ex]["results"][res].status;
						break;
					}
				}
			}
		}

		switch(status){
		case "-1":
			return "Not Started"
		case "1":
			return "Vulnerable"
		case "0":
			return "Not Vulnerable"
		case "2":
			return "Broken Functionality"
		case "4":
			return "Not Addressed"
		default: return "N/A"
		};
	}
	$scope.getClassForChallengeResult = function(user,flag){
		var status = $scope.getChallengeResultFor(user,flag);

		switch(status){
		case "-1":
			return ""
		case "Vulnerable":
			return "table-danger"
		case "Not Vulnerable":
			return "table-success"
		case "Broken Functionality":
			return "table-warning"
		case "Not Addressed":
			return "table-info"
		default: return "N/A"
		};



	}
	$scope.backToList = function(){
		$rootScope.showChallengeDetails = false;
		$rootScope.showChallengesList = true;
	}


	$scope.getChallengeStatusString = function(status){
		switch(status){
		case "IN_PROGRESS":
			return "In Progress";
			break;
		case "NOT_STARTED":
			return "Not Started";
			break;
		case "FINISHED":
			return "Finished";
			break;
		}
	}


	$scope.updateChallengesFilteredList = function() {
		$scope.filteredChallengesList = $filter("filter")($scope.masterChallengesList, $scope.queryChallenges);
	};
	$scope.challengestableconfig = {
			itemsPerPage: 20,
			fillLastPage: false
	}
	$scope.$on('challenges:updated', function(event,data) {
		$scope.masterChallengesList = data;
		$scope.filteredChallengesList = $scope.masterChallengesList;
	});

}]);
