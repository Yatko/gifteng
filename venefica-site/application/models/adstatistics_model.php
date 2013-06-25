<?php

/**
 * Description of Ad statistics DTO
 *
 * @author gyuszi
 */
class AdStatistics_model extends CI_Model {
    
    var $numAvailProlongations = 0; //int
    var $numViews = 0; //long
    var $numComments = 0; //long
    var $numBookmarks = 0; //long
    var $numShares = 0; //long
    var $rating = 0; //float
    
    public function __construct($obj = null) {
        log_message(DEBUG, "Initializing AdStatistics_model");
        
        if ( $obj != null ) {
            $this->numAvailProlongations = getField($obj, 'numAvailProlongations');
            $this->numViews = getField($obj, 'numViews');
            $this->numComments = getField($obj, 'numComments');
            $this->numBookmarks = getField($obj, 'numBookmarks');
            $this->numShares = getField($obj, 'numShares');
            $this->rating = getField($obj, 'rating');
        }
    }
    
    // static helpers
    
    public static function convertAdStatistics($statistics) {
        return new AdStatistics_model($statistics);
    }
}