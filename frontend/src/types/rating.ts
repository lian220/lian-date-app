export interface Rating {
  ratingId: string;
  courseId: string;
  score: number;
  createdAt: string;
}

export interface RateCourseRequest {
  score: number;
  sessionId: string;
}

export interface RatingWidgetProps {
  courseId: string;
  onRatingComplete?: (rating: Rating) => void;
}
