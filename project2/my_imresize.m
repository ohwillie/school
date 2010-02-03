function b = my_imresize(a,m,n)
%MY_IMRESIZE Resize image with antialiasing.
%  B = MY_IMRESIZE(A,M,N) resizes image A to be M-by-N using bilinear 
%  interpolation.  Before performing the interpolation, A is blurred with 
%  a Gaussian in each dimension to prevent aliasing.

[height width channels] = size(a);

% determine the size of the Gaussian blur kernel
sigma1 = 0.5 * height / m;
sigma2 = 0.5 * width / n;
k1 = ceil(3 * sigma1);
k2 = ceil(3 * sigma2);

% construct the (separable) kernel
d1 = linspace(-k1,k1,2*k1+1).^2 / sigma1^2;
d2 = linspace(-k2,k2,2*k2+1).^2 / sigma2^2;
h1 = exp(-d1)';
h2 = exp(-d2);
h1 = h1 / sum(h1);
h2 = h2 / sum(h2);

% allocate space for the resized image
b = zeros(m,n,channels);

for i=1:channels
  % pad the image by duplicating the boundary
  temp = [repmat(a(1,1,i),k1,k2)   repmat(a(1,:,i),k1,1)   repmat(a(1,end,i),k1,k2);
          repmat(a(:,1,i),1,k2)    a(:,:,i)                repmat(a(:,end,i),1,k2);
          repmat(a(end,1,i),k1,k2) repmat(a(end,:,i),k1,1) repmat(a(end,end,i),k1,k2)];

  % blur this image channel with the kernel
  temp = conv2(temp,h1,'full');
  temp = conv2(temp,h2,'full');

  % perform the bilinear interpolation
  x = linspace(2*k2+0.5, width+2*k2+0.5, 2*n+1);
  y = linspace(2*k1+0.5, height+2*k1+0.5, 2*m+1);
  [x y] = meshgrid(x(2:2:end-1),y(2:2:end-1));
  b(:,:,i) = interp2(temp,x,y,'linear');
end
