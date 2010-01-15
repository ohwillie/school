function [ B ] = my_conv2( A, H )

% Extra credit: separate a filter if it is separable.
if rank(H) == 1
  [C, S, D] = svd(H);
  rt = sqrt(S(1, 1));
  c = C(:, 1) * rt;
  d = D(:, 1) * rt;
  B = helper(helper(A, c), d');
else
  B = helper(A, H);
end

% Fix boundary violations...
mi = min(min(min(B)));
if mi < 0
  B = B - mi;
end

ma = max(max(max(B)));
if ma > 1
  B = B / ma;
end

end
  
function [ B ] = helper( A, H )
[r, c, dims] = size(A);
[fr, fc] = size(H);
kr = floor(fr / 2);
kc = floor(fc / 2);
Hf = fliplr(flipud(H));
B = zeros(r, c, dims);
for d = 1:dims
  for i = 1:r
    for j = 1:c
      for k = -kr:kr
        for l = -kc:kc
          ipk = i + k;
          jpl = j + l;
          if ipk < 1 || ipk > r || jpl < 1 || jpl > c
            continue
          end
          B(i, j, d) = B(i, j, d) + A(ipk, jpl, d)... 
                     * Hf(k + fr - kr, l + fc - kc);
        end
      end
    end
  end
end
end