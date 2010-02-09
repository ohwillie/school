function [] = draw_faces(img, x, y, s, siz)
[rx cx] = size(x);
[ry cy] = size(y);
[rs cs] = size(s);
if nargin < 5
  siz = [49 49];
end
if cx ~= cy || cx ~= cs
  error('x, y, and s must be the same length!');
end
[r c d] = size(img);
imshow(img);
for i = 1:cx
  rectangle('Position', [x(i), y(i), s(i) * siz(1), s(i) * siz(2)],...
            'EdgeColor', 'green');
end
end