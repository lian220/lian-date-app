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
        InfoWindow: new (options: InfoWindowOptions) => InfoWindow;
        LatLngBounds: new () => LatLngBounds;
        event: {
          addListener: (target: any, eventName: string, callback: Function) => void;
          removeListener: (target: any, eventName: string, callback: Function) => void;
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
  addListener: (eventName: string, callback: Function) => void;
}

export interface MarkerOptions {
  position: LatLng;
  map?: KakaoMap;
  title?: string;
  image?: MarkerImage;
  clickable?: boolean;
}

export interface Marker {
  setMap: (map: KakaoMap | null) => void;
  getMap: () => KakaoMap | null;
  getPosition: () => LatLng;
  setPosition: (latlng: LatLng) => void;
  addListener: (eventName: string, callback: Function) => void;
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
}
