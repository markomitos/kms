import {Key} from "./key.model";

export interface KeyVersion{
  id?: number,
  version?: number,
  encryptedKeyMaterial?: string,
  publicKeyMaterial?: string,
  createdAt?: string,
  enabled?: boolean
  key?: Key;
}
