/**
 * 카카오맵 SDK 타입 정의
 */

declare global {
  interface Window {
    kakao: {
      maps: {
        LatLng: new (lat: number, lng: number) => LatLng;
        Map: new (container: HTMLElement, options: MapOptions) => KakaoMap;
        Marker: new (options: MarkerOptions) => Marker;
        MarkerImage: new (
          src: string,
          size: Size,
          options?: MarkerImageOptions
        ) => KakaoMarkerImage;
        InfoWindow: new (options: InfoWindowOptions) => InfoWindow;
        LatLngBounds: new () => LatLngBounds;
        Polyline: new (options: PolylineOptions) => Polyline;
        CustomOverlay: new (options: CustomOverlayOptions) => CustomOverlay;
        Size: new (width: number, height: number) => Size;
        Point: new (x: number, y: number) => Point;
        event: {
          addListener: (
            target: unknown,
            eventName: string,
            callback: (this: unknown) => void
          ) => void;
          removeListener: (
            target: unknown,
            eventName: string,
            callback: (this: unknown) => void
          ) => void;
        };
      };
    };
  }
}

export interface LatLng {
  getLat: () => number;
  getLng: () => number;
}

export interface MapOptions {
  center: LatLng;
  level: number;
  draggable?: boolean;
  scrollwheel?: boolean;
}

export interface KakaoMap {
  setCenter: (latlng: LatLng) => void;
  getCenter: () => LatLng;
  setLevel: (level: number) => void;
  getLevel: () => number;
  panTo: (latlng: LatLng) => void;
  getBounds: () => { contain: (latlng: LatLng) => boolean };
  setBounds: (bounds: LatLngBounds) => void;
  addListener: (eventName: string, callback: (this: unknown) => void) => void;
}

export interface MarkerOptions {
  position: LatLng;
  map?: KakaoMap | null;
  title?: string;
  image?: MarkerImage;
  clickable?: boolean;
}

export interface Marker {
  setMap: (map: KakaoMap | null) => void;
  getMap: () => KakaoMap | null;
  getPosition: () => LatLng;
  setPosition: (latlng: LatLng) => void;
  addListener: (eventName: string, callback: (this: unknown) => void) => void;
}

export interface MarkerImage {
  src: string;
  size: { width: number; height: number };
  options?: { offset?: { x: number; y: number } };
}

export interface InfoWindowOptions {
  content: string | HTMLElement;
  position?: LatLng;
  map?: KakaoMap;
  removable?: boolean;
}

export interface InfoWindow {
  open: (map: KakaoMap, marker: Marker) => void;
  close: () => void;
  setMap: (map: KakaoMap | null) => void;
}

export interface LatLngBounds {
  extend: (latlng: LatLng) => void;
  contain: (latlng: LatLng) => boolean;
  getNorthEast: () => LatLng;
  getSouthWest: () => LatLng;
}

export interface MapPlace {
  id: string;
  name: string;
  lat: number;
  lng: number;
  address?: string;
  category?: string;
  order?: number;
}

export interface Size {
  width: number;
  height: number;
}

export interface Point {
  x: number;
  y: number;
}

export interface MarkerImageOptions {
  offset?: Point;
  alt?: string;
  coords?: string;
  shape?: string;
  spriteOrigin?: Point;
  spriteSize?: Size;
}

// Kakao Maps MarkerImage 객체 (내부 구현은 SDK에서 제공)
export type KakaoMarkerImage = object;

export interface PolylineOptions {
  map?: KakaoMap | null;
  path: LatLng[];
  strokeWeight?: number;
  strokeColor?: string;
  strokeOpacity?: number;
  strokeStyle?: 'solid' | 'shortdash' | 'shortdot' | 'shortdashdot' | 'shortdashdotdot' | 'dot' | 'dash' | 'dashdot' | 'longdash' | 'longdashdot' | 'longdashdotdot';
  endArrow?: boolean;
}

export interface Polyline {
  setMap: (map: KakaoMap | null) => void;
  getMap: () => KakaoMap | null;
  setPath: (path: LatLng[]) => void;
  getPath: () => LatLng[];
  setOptions: (options: Partial<PolylineOptions>) => void;
}

export interface CustomOverlayOptions {
  map?: KakaoMap | null;
  position: LatLng;
  content: string | HTMLElement;
  xAnchor?: number;
  yAnchor?: number;
  zIndex?: number;
  clickable?: boolean;
}

export interface CustomOverlay {
  setMap: (map: KakaoMap | null) => void;
  getMap: () => KakaoMap | null;
  setPosition: (position: LatLng) => void;
  getPosition: () => LatLng;
  setContent: (content: string | HTMLElement) => void;
  setZIndex: (zIndex: number) => void;
}
