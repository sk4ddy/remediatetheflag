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
function responsiveView() {
        var win = $(window).height();
        var n = parseInt($('.navbar').css('height').replace('px',''))
        var f = parseInt($('#footerwrap').css('height').replace('px',''))
        var h = parseInt($('#headerwrap').css('height').replace('px',''))
        $('.serviceContainer').css('height',(win - n - f - h + 10)+"px");
}
$(document).ready(function() {
    $('.waitLoader').hide();
    window.cookieconsent.initialise({
		 "palette": {
			 "popup": {
			      "background": "#edeff5",
			      "text": "#838391"
			    },
			    "button": {
			      "background": "#4b81e8"
			    }
			  }
	})
  
    
    
	$('#loginButton').click(function(e){
		e.preventDefault();
		e.stopPropagation();
        $('.waitLoader').show();
        var payload = {};
        payload.username = $('#username').val();
        payload.password = $('#password').val();
        payload.action = "doLogin";
		$.ajax({
			type: "POST",
			url: '/handler',
            contentType: 'application/json',
			data: JSON.stringify(payload),
			success: function(obj) {
				try{
                    obj = JSON.parse(obj);
                }catch(err){}
                if(obj.result=="error"){
                    $('#accountLockout').show();
                    $('#password').val("");
                    $('#loginButton').attr('disabled','disabled');
                    $('#newMember').hide();
                    $('.waitLoader').hide();
                }
                else if(obj.result=="redirect"){
                    if(obj.location=="/index.html"){
                            $('#incorrectLogin').show();
                            $('#password').val("");
                            $('.waitLoader').hide();
                            setTimeout(function(){$("#incorrectLogin").hide()},5000);
				    }
                    else{
                        $(document).attr('location', obj.location);
                    }
                }
                
				
			}
		});
	});
    $(document).keyup(function(event){
        if(event.keyCode == 13){
             $("#loginButton").click();
            }
    });
    
    responsiveView();
    $(window).on('load', responsiveView);
    $(window).on('resize', responsiveView); 
});