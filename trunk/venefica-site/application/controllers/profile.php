<?php

class Profile extends CI_Controller {
    
    private $initialized = false;
    
    public function view($name = null) {
        $this->init();
        
        $user = null;
        $receivings = null;
        $givings = null;
        $bookmarks = null;
        $followers = null;
        $followings = null;
        $ratings = null;
        
        $givings_num = 0;
        $receivings_num = 0;
        $bookmarks_num = 0;
        $followers_num = 0;
        $followings_num = 0;
        $ratings_num = 0;
        
        if ( isLogged() ) {
            try {
                if ( !empty($name) ) {
                    $user = $this->usermanagement_service->getUserByName($name);
                } else {
                    $user = $this->usermanagement_service->loadUser();
                }
            } catch ( Exception $ex ) {
            }
            
            try {
                $receivings = $this->ad_service->getUserRequestedAds($user->id);
            } catch ( Exception $ex ) {
            }
            
            try {
                $givings = $this->ad_service->getUserAds($user->id);
            } catch ( Exception $ex ) {
            }
            
            try {
                $bookmarks = $this->ad_service->getBookmarkedAds($user->id);
            } catch ( Exception $ex ) {
            }
            
            try {
                $followers = $this->usermanagement_service->getFollowers($user->id);
            } catch ( Exception $ex ) {
            }
            try {
                $followings = $this->usermanagement_service->getFollowings($user->id);
            } catch ( Exception $ex ) {
            }
            
            try {
                $ratings = $this->ad_service->getReceivedRatings($user->id);
            } catch ( Exception $ex ) {
            }
        }
        
        if ( $receivings ) {
            $receivings_num = count($receivings);
        }
        if ( $givings ) {
            $givings_num = count($givings);
        }
        if ( $bookmarks ) {
            $bookmarks_num = count($bookmarks);
        }
        if ( $followers ) {
            $followers_num = count($followers);
        }
        if ( $followings ) {
            $followings_num = count($followings);
        }
        if ( $ratings ) {
            $ratings_num = count($ratings);
        }
        
        $data = array();
        $data['isLogged'] = isLogged();
        $data['is_ajax'] = false;
        $data['user'] = $user;
        $data['receivings'] = $receivings;
        $data['givings'] = $givings;
        $data['followers'] = $followers;
        $data['followings'] = $followings;
        $data['ratings'] = $ratings;
        
        $data['givings_num'] = $givings_num;
        $data['receivings_num'] = $receivings_num;
        $data['bookmarks_num'] = $bookmarks_num;
        $data['followers_num'] = $followers_num;
        $data['followings_num'] = $followings_num;
        $data['ratings_num'] = $ratings_num;
        
        $this->load->view('templates/'.TEMPLATES.'/header');
        $this->load->view('pages/profile', $data);
        $this->load->view('templates/'.TEMPLATES.'/footer');
    }
    
    public function ajax() {
        $this->init();
        
        if ( !isLogged() ) {
            return;
        }
        
        $data = array();
        $data['isLogged'] = isLogged();
        $data['is_ajax'] = true;
        
        $this->load->view('pages/profile', $data);
    }
    
    // internal
    
    private function init() {
        if ( !$this->initialized ) {
            //load translations
            $this->lang->load('main');
            $this->lang->load('profile');
            
            $this->load->library('auth_service');
            $this->load->library('ad_service');
            $this->load->library('usermanagement_service');
            
            $this->load->model('image_model');
            $this->load->model('address_model');
            $this->load->model('ad_model');
            $this->load->model('user_model');
            $this->load->model('rating_model');
            
            $this->initialized = true;
        }
    }
}