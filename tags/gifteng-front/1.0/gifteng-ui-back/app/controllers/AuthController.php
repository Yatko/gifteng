<?php

class AuthController extends \BaseController {
	 
	 /**
	  * Provides user details
	  * 
	  * @return Response
	  */ 
	  public function index() {
	  	if(Session::get('user.token')) {
	  		return array_merge(Session::get('user'),array('logged'=>true));
	  	}
		else {
			return array('logged'=>false);
		}
	  }
	 
	 /**
	  * Authenticate the user
	  * 
	  * @return Response
	  */
	  public function store() {
	  	try {
		  	$authService = new SoapClient(Config::get('wsdl.auth'));
	        $token = $authService->authenticateEmail(
	        	array(
	        		"email" => Input::get('email'),
	            	"password" => Input::get('password'),
	            	"userAgent" => NULL
				)
			);
	        ini_set('soap.wsdl_cache_enabled', '0');
			ini_set('user_agent', "PHP-SOAP/".PHP_VERSION."\r\n"."AuthToken: ".$token->AuthToken);
			Session::put('user.token', $token);
			
	        try {
	            $userService = new SoapClient(Config::get('wsdl.user'));
	            $result = $userService->getUserByEmail(array("email" => Input::get('email')));
	            $user = $result->user;
				if($user->businessAccount==true) {
					if(isset($user->addresses) && is_object($user->addresses)) {
						$user->addresses = array($user->addresses);
					}
				}
				Session::put('user.data',$user);
			
				return array('success'=>'true');
				
	        } catch ( InnerException $ex ) {
	            throw new Exception($ex->faultstring);
	        }
	    } catch ( Exception $ex ) {
	        return array('error'=>$ex->faultstring);
	    }
	  }
	  
	  /**
	   * Destroy user session
	   * 
	   * @return Response
	   */
	   public function destroy($id) {
	  	if(Session::get('user.token')) {
	  		Session::flush();
	  	}
		return array('logged'=>false);
	   }

		/**
		 *
		 * @return Response
		 * @throws Exception
		 */
		public function changeForgottenPassword() {
			try {
				$authService = new SoapClient(Config::get('wsdl.auth'));
				$authService -> changeForgottenPassword(array("newPassword" => Input::get('password'), "code" => Input::get('code')));
				return array('status' => 'true');
			} catch ( Exception $ex ) {
				return array('status' => 'false');
			}
		}
		 
}