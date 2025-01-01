const cacheName = "cache1"; // Change value to force update
self.addEventListener("install", event => {
// Kick out the old service worker
self.skipWaiting();
event.waitUntil(
caches.open(cacheName).then(cache => {
return cache.addAll([
"/",
"index.html",
"script.js",
"style.css",
]);
})
);
});
self.addEventListener("activate", event => {
    // Delete any non-current cache
    event.waitUntil(
    caches.keys().then(keys => {
    Promise.all( 
    keys.map(key => {
    if (![cacheName].includes(key)) {
    return caches.delete(key);
    }
    })
    )
    })
    );
});
self.addEventListener("fetch", event => {
    event.respondWith(
    caches.open(cacheName).then(cache => {
    return cache.match(event.request).then(response => {
    return response || fetch(event.request).then(networkResponse => {
    cache.put(event.request, networkResponse.clone());
    return networkResponse;
    });
    })
    })
    );
    });

