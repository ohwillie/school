function [ coeffs ] = project_face( avgface, eigfaces, newface )
[r c d] = size(eigfaces);
c = cell(d, 1);
corrected = newface - avgface;
for i = 1:d
  c{d} = corrected * eigfaces(:, :, d);
end
acc = avgface
end

