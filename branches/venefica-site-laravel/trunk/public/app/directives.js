define(['angular','services','jquery'], function(angular,services,jQuery) {
	'use strict';
	
	return angular.module('gifteng.directives', ['gifteng.services'])
		.directive('adItemBox', function($compile) {
			return {
				restrict:'E',
				scope: {
					img:'@',
					status:'@',
					type:'@',
					creatorId:'@',
					creatorName:'@',
					creatorSince:'@',
					creatorFollowing:'=',
					creatorCity:'@',
					creatorAvatar:'@',
					creatorPoints:'@',
					id:'@',
					ad:'='
				},
				templateUrl: 'app/partials/directives/ad-item-box.html',
				replace:true,
				transclude:true,
				compile: function compile() {
					return {
				        post: function (scope, iElement, iAttrs) { 
				        	
				        	iAttrs.$observe('status', function() {
								var status = scope.status;
								var type = iAttrs.type;
								var action = '';
								var text = '';
								
								if(iAttrs.details=='1' || type=="details") {
									var userprofile = '<user-profile name="{{creatorName}}" id="{{creatorId}}" location="{{creatorCity}}" img="https://s3.amazonaws.com/ge-dev/user/{{creatorAvatar}}_60" points="{{creatorPoints}}" since="{{creatorSince}}" following="creatorFollowing" nested="true"></user-profile>';
									$('.well',iElement).prepend($compile(userprofile)(scope));
								}
								
							});
						}
				      }
				}
			}
		})
		.directive('userProfile', function() {
			return {
				restrict:'E',
				scope: {
					id:'@',
					img:'@',
					name:'@',
					location:'@',
					since:'@',
					points:'@',
					nested:'@',
					ads:'@',
					following:'=',
					self:'@'
				},
				templateUrl: 'app/partials/directives/user-profile.html',
				replace:true,
				controller: function( $scope, $element, $attrs, UserEx ) {
					$scope.follow = function() {
						$scope.data = UserEx.follow.query({id:$scope.id});
						$attrs.$set('following',true);
						$scope.following=true;
					}
					$scope.unfollow = function() {
						$scope.data = UserEx.unfollow.query({id:$scope.id});
						$attrs.$set('following',false);
						$scope.following=false;
					}
					$attrs.$observe('following', function(value) {
						if(typeof value=="Boolean")
							$scope.following=value;
					});
				},
				link: function($scope, $element, $attrs) {
					$attrs.$observe('following', function(value) {
						$scope.following=value;
					});
				},
				compile: function() {
					return {
						post: function(scope, iElement, iAttrs) {
							if(iAttrs.showgifts) {
								iAttrs.$observe('ads', function() {
									if(scope.ads!='undefined' && scope.ads!="") {
										var ads = angular.fromJson(scope.ads);
										var gifts = '<div class="row">';
										for(var i=0;i<ads.length;i++) {
											gifts += '<div class="col-xs-4">'+
														'<a href="#/view/gift/'+ads[i].id+'"><img src="https://s3.amazonaws.com/ge-dev/ad/'+ads[i].image.id+'_320" class="img-rounded img-responsive" alt=""></a>'+
													'</div>';
										}
										gifts += '</div>';
									
										$('.well',iElement).append(gifts);
										
									}
								});
							}
							if(iAttrs.nested) {
								$('.well',iElement).removeClass('well');
							}
						}
					}
				}
			}
		})
		.directive('ad', function() {
			return {
				restrict:'E',
				scope: {
					img:'@',
					title:'@',
					simple:'@',
					numShares: '@',
					numComments: '@',
					numBookmarks: '@',
					comments:'@',
					creatorId:'@',
					creatorName:'@',
					creatorSince:'@',
					creatorFollowing:'=',
					creatorCity:'@',
					creatorAvatar:'@',
					creatorPoints:'@',
					inBookmarks:'@',
					id:'@',
					status:'@',
					canRequest:'@',
					category:'@'
				},
				templateUrl: 'app/partials/directives/ad.html',
				replace:true,
				transclude:true,
				controller: function($scope, $modal, UserEx) {
					$scope.bookmark = function(id) {
						UserEx.bookmark.query({id:id});
						$scope.numBookmarks++;
						$scope.inBookmarks=true;
					}
					$scope.unbookmark = function(id) {
						UserEx.unbookmark.query({id:id});
					}
					$scope.doComment = function (id) {
					    var modalInstance = $modal.open({
      						templateUrl: 'app/partials/directives/modal/comment.html',
      						controller: function($scope, $modalInstance) {
      							$scope.add = function () {
      								UserEx.comment.query({id:id,text:$('#comment_text').val()});
									$modalInstance.close();
								};
      						}
      					});
					  };
				},
				link: function() {
					return {
						pre: function(scope, iElement, iAttrs) {
							if(iAttrs.simple) {
								$('.ge-action',iElement).remove();
								$('.title',iElement).remove();
								$('user-profile',iElement).remove();
							}
				        	iAttrs.$observe('comments', function() {
				        		if(scope.numComments>0) {
					        		scope.comments = angular.fromJson(scope.comments);
					        		if(typeof(scope.comments.type) !== 'undefined') {
					        			scope.comments = [scope.comments];
					        		}
				        		}
				        	});
						}
					}
				}
			}
		})
		.directive('message', function() {
			return {
				restrict:'E',
				templateUrl: 'app/partials/directives/message.html',
				scope: {
      				ngModel: '='
				},
				replace:true,
    			require: 'ngModel',
			    controller: function($scope, UserEx) {
			    	$scope.sendMsg = function() {
			    		var message = new UserEx.message();
			            message.text = $('#text_msg').val();
			            message.requestId = $scope.ngModel.messages[0].requestId;
			            message.toId = $scope.ngModel.messages[0].toId;
			            message.$save();
			    	};
			    }
			}
		})
		.directive('backButton', function(){
		    return {
		      restrict: 'A',
		
		      link: function(scope, element, attrs) {
		        element.bind('click', goBack);
		
		        function goBack() {
		          history.back();
		          scope.$apply();
		        }
		      }
		    }
		})
		.directive('giftimg', function() {
			return {
				restrict: 'E',
				replace: false,
				templateUrl: 'app/partials/directives/giftimg.html',
				scope: {
					action: '@',
					def: '@'
				},
				controller: function ($scope) {
					$scope.progress = 0;
					$scope.giftimage = 'http://veneficalabs.com/gifteng/assets/4/temp-sample/ge-upload.png';
				
					$scope.sendFile = function(el) {
				
						var $form = $(el).parents('form');
				
						if ($(el).val() == '') {
							return false;
						}
				
						$form.attr('action', $scope.action);
				
						$scope.$apply(function() {
							$scope.progress = 0;
						});				
				
						$form.ajaxSubmit({
							type: 'POST',
							uploadProgress: function(event, position, total, percentComplete) { 
								
								$scope.$apply(function() {
									$scope.progress = percentComplete;
								});
				
							},
							error: function(event, statusText, responseText, form) { 
								
								$form.removeAttr('action');
				
							},
							success: function(responseText, statusText, xhr, form) { 
				
								var ar = $(el).val().split('\\'), 
									filename =  ar[ar.length-1];

								$form.removeAttr('action');
				
								$scope.$apply(function() {
									$scope.progress = 0;
									$scope.giftimage = "api/image/"+filename;
								});
				
							},
						});
				
					}
				
				},
				link: function(scope, elem, attrs, ctrl) {
					elem.find('.fake-uploader').click(function() {
						elem.find('input[type="file"]').click();
					});
				}
			};
		
		});
});
