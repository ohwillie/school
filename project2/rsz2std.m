function [faces, m] = rsz2std(faces)
len = length(faces); m = [Inf, Inf];
for i = 1:len
  siz = size(faces{i});
  m = min(siz, m);
end
for i = 1:len
  faces{i} = my_imresize(faces{i}, m(1), m(2));
end
end