$(function() {

	var slideNav = {
		interval: null,
		isPaused: false,
		play: function() {
			this.isPaused = false;
			this.interval = setInterval(function() {
				slideNav.playSlideshow();
			}, 3000);
		},
		pause: function() {
			clearInterval(this.interval);
			this.isPaused = true;
		},
		isCurrent: function(target) {
			return ($(target).hasClass('currentNav')) ? true : false;
		},
		markCurrent: function(target) {
			var $parent = $(target).parent(),
				$current = $parent.find('li.currentNav');

			$current.removeClass('currentNav');
			$(target).addClass('currentNav');

			this.pickupCurrentImage(target);
		},
		moveNaviBar: function(target) {
			var $bar = $('#jsiNaviBar'),
				targetTop = (!target) ? $('#jsiImageNavi').find('li.currentNav').position().top : $(target).position().top;

			$bar.stop(false, true).animate({top: targetTop});
		},
		pickupCurrentImage: function(target) {
			var idx = $(target).index(),
				$mainImg = $('#jsiMainImage'),
				$mainImgList = $mainImg.find('li'),
				$current = $mainImg.find('li.current');
				$nextCurrent = $mainImgList.eq(idx);

			this.animateImg($current, $nextCurrent);
		},
		changeImg: function($currentImg, $nextCurrentImg) {
			$currentImg.removeClass('current prevCurrent');
			$nextCurrentImg.addClass('current').css({opacity: 1});
		},
		fadeImg: function($currentImg, $nextCurrentImg) {
			$currentImg.addClass('prevCurrent');
			$nextCurrentImg.css({opacity: 0}).addClass('current').stop(true, true).animate({opacity: 1}, 1000, function() {
				$currentImg.removeClass('current prevCurrent');
			});
		},
		animateImg: function($current, $nextCurrent) {
			var $currentImg, $nextCurrentImg,
				$mainImg = $('#jsiMainImage');

			if (!$current && !$nextCurrent) {
				$currentImg = $mainImg.find('li.current').length ? $mainImg.find('li.current') : $mainImg.find('li').last();
				$nextCurrentImg = $mainImg.find('li.current').next().length ? $mainImg.find('li.current').next() : $mainImg.find('li').first();
				this.fadeImg($currentImg, $nextCurrentImg);
			} else {
				$currentImg = $current;
				$nextCurrentImg = $nextCurrent;
				this.changeImg($currentImg, $nextCurrentImg);
			}

		},
		animateNav: function() {
			var $nav = $('#jsiImageNavi');
				$currentNav = $nav.find('li.currentNav').length ? $nav.find('li.currentNav') : $nav.find('li').last(),
				$nextNav = $currentNav.next().length ? $currentNav.next() : $nav.find('li').first();

			$currentNav.removeClass('currentNav');
			$nextNav.addClass('currentNav');
		},
		playSlideshow: function() {
			this.animateImg();
			this.animateNav();
			this.moveNaviBar();
		}
	};


	var $mainImg = $('#jsiMainImage'),
		$imageNavi = $('#jsiImageNavi');

	slideNav.play();

	$mainImg.mouseover(function() {
		slideNav.pause();
	});
	$mainImg.mouseout(function() {
		if (slideNav.isPaused) {
			slideNav.play();
		}
	});

	$imageNavi.mouseover(function() {
		slideNav.pause();
	});
	$imageNavi.delegate('li', 'mouseover', function() {
		if (!slideNav.isCurrent(this) && slideNav.isPaused) {
			slideNav.markCurrent(this);
			slideNav.moveNaviBar(this);
		}
	});

	$imageNavi.delegate('li.currentNav', 'mouseout', function() {
		if (slideNav.isPaused) {
			slideNav.play();
		}
	});

});